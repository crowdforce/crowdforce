package space.crowdforce.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import space.crowdforce.AbstractIT
import space.crowdforce.controllers.model.ProjectFormUI
import space.crowdforce.dsl.GiveMe
import space.crowdforce.service.project.ProjectService
import javax.inject.Inject

class ProjectControllerIT : AbstractIT() {
    @Inject
    private lateinit var webTestClient: WebTestClient

    @Inject
    private lateinit var giveMe: GiveMe

    @Inject
    private lateinit var projectService: ProjectService

    @BeforeEach
    internal fun setUp() = giveMe.emptyDatabase()

    @WithMockUser(username = TEST_USER)
    @Test
    fun `Should return list of projects with auth user`() {
        // given:
        giveMe.user(TEST_USER).please()

        val expected = giveMe.authorized(TEST_USER)
            .project(ownerName = TEST_USER)
            .and()
            .project(ownerName = TEST_USER).witSubscriber(TEST_USER)
            .pleaseJson()

        // act and check:
        webTestClient.get()
            .uri("/api/v1/projects")
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody().json(expected)
    }

    @Test
    fun `Should return list of projects`() {
        // given:
        giveMe.user(TEST_USER).please()

        val expected = giveMe.unauthorized()
            .project(ownerName = TEST_USER)
            .and()
            .project(ownerName = TEST_USER).witSubscriber(TEST_USER)
            .pleaseJson()

        // act and check:
        webTestClient.get()
            .uri("/api/v1/projects")
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody().json(expected)
    }

    @Test
    fun `Should add a project unauthorized user`() {
        // given:
        giveMe.user(TEST_USER).please()

        // act and check:
        webTestClient.post()
            .uri("/api/v1/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(ProjectFormUI("test", "test", 123.123, 321.321))
            .exchange()
            .expectStatus().is5xxServerError
    }

    @WithMockUser(username = TEST_USER)
    @Test
    fun `Should add a project`() {
        // given:
        giveMe.user(TEST_USER).please()

        val projectForm = ProjectFormUI("test", "test", 123.123, 321.321)

        // act and check:
        webTestClient.post()
            .uri("/api/v1/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(projectForm)
            .exchange()
            .expectStatus().is2xxSuccessful // TODO rest statuses evrywhere
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.name").isEqualTo(projectForm.name)
            .jsonPath("$.description").isEqualTo(projectForm.description)
            .jsonPath("$.lat").isEqualTo(projectForm.lat)
            .jsonPath("$.lng").isEqualTo(projectForm.lng)
    }

    @WithMockUser(username = TEST_USER)
    @Test
    fun `Should update project fields`() {
        // given:
        giveMe.user(TEST_USER).please()
        val project = giveMe.authorized(TEST_USER).project(TEST_USER).please()[0]

        val projectForm = ProjectFormUI("updated", "updated", 123.123, 321.321)

        // act and check:
        webTestClient.put()
            .uri("/api/v1/projects/${project.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(projectForm)
            .exchange()
            .expectStatus().is2xxSuccessful // TODO rest statuses evrywhere

        assertThat(projectService.findProject(project.id))
            .satisfies {
                assertThat(it!!.name).isEqualTo(projectForm.name)
                assertThat(it.description).isEqualTo(projectForm.description)
                assertThat(it.location.latitude).isEqualTo(projectForm.lat)
                assertThat(it.location.longitude).isEqualTo(projectForm.lng)
            }
    }

    @Test
    fun `Should update project fields unauthorized user`() {
        // given:
        giveMe.user(TEST_USER).please()

        val projectForm = ProjectFormUI("test", "test", 123.123, 321.321)

        // act and check:
        webTestClient.post()
            .uri("/api/v1/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(projectForm)
            .exchange()
            .expectStatus().is5xxServerError
    }

    @WithMockUser(username = TEST_USER)
    @Test
    fun `Should update project fields not owner`() {
        // given:
        val userName = "another_test_user"
        giveMe.user(userName).please()
        giveMe.user(TEST_USER).please()
        val project = giveMe.authorized(TEST_USER).project(userName).please()[0]

        val projectForm = ProjectFormUI("updated", "updated", 123.123, 321.321)

        // act and check:
        webTestClient.put()
            .uri("/api/v1/projects/${project.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(projectForm)
            .exchange()
            .expectStatus().is5xxServerError // TODO rest statuses evrywhere
    }

    @WithMockUser(username = TEST_USER)
    @Test
    fun `Should delete project`() {
        // given:
        giveMe.user(TEST_USER).please()

        val project = giveMe.authorized(TEST_USER).project(TEST_USER).please()[0]

        // act and check:
        webTestClient.delete()
            .uri("/api/v1/projects/${project.id}")
            .accept(MediaType.APPLICATION_JSON)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk

        assertThat(projectService.findProject(project.id))
            .isNull()
    }

    @Test
    fun `Should delete project unauthorized user`() {
        // given:
        giveMe.user(TEST_USER).please()
        val project = giveMe.unauthorized().project(TEST_USER).please()[0]

        // act:
        webTestClient.delete()
            .uri("/api/v1/projects/${project.id}")
            .accept(MediaType.APPLICATION_JSON)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().is5xxServerError
    }

    @WithMockUser(username = TEST_USER)
    @Test
    fun `Should delete project not owner`() {
        // given:
        val userName = "another_test_user"
        giveMe.user(TEST_USER).please()
        giveMe.user(userName).please()
        val project = giveMe.authorized(userName).project(userName).please()[0]

        // act:
        webTestClient.delete()
            .uri("/api/v1/projects/${project.id}")
            .accept(MediaType.APPLICATION_JSON)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().is5xxServerError
    }
}
