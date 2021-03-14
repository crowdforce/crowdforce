package space.crowdforce.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import space.crowdforce.AbstractIT
import space.crowdforce.WithMockUserIdentity
import space.crowdforce.controllers.model.ProjectFormUI
import space.crowdforce.domain.UserIdentity
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

    @Inject
    private lateinit var objectMapper: ObjectMapper

    @AfterEach
    internal fun cleanUp() = giveMe.emptyDatabase()

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should return list of projects with auth user`() {
        // given:
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id

        val expected = giveMe.authorized(userId)
            .project(ownerId = userId)
            .witSubscriber(userId)
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
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id

        val expected = giveMe.unauthorized()
            .project(ownerId = userId)
            .witSubscriber(userId)
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
        giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please()

        // act and check:
        webTestClient.post()
            .uri("/api/v1/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(ProjectFormUI("test", "test", 123.123, 321.321))
            .exchange()
            .expectStatus().is5xxServerError
    }

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should add a project`() {
        // given:
        giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please()

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

    @Test
    fun `Should get project`() {
        // given:
        val ownerId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id

        val project = giveMe.unauthorized()
            .project(ownerId = ownerId)
            .please()[0]

        val projectJson = objectMapper.writeValueAsString(project)

        // act and check:
        webTestClient.get()
            .uri("/api/v1/projects/${project.id}")
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody().json(projectJson)
    }

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should get project with subscription`() {
        // given:
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id

        val project = giveMe.authorized(userId)
            .project(ownerId = userId)
            .witSubscriber(userId)
            .please()[0]

        val projectJson = objectMapper.writeValueAsString(project)

        // act and check:
        webTestClient.get()
            .uri("/api/v1/projects/${project.id}")
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody().json(projectJson)
    }

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should update project fields`() {
        // given:
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id
        val project = giveMe.authorized(userId).project(userId).please()[0]

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
        giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please()

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

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should update project fields not owner`() {
        // given:
        val ownerId = giveMe.user(UserIdentity.TG.identityKey("4343")).please().id
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id
        val project = giveMe.authorized(userId).project(ownerId).please()[0]

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

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should delete project`() {
        // given:
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id

        val project = giveMe.authorized(userId).project(userId).please()[0]

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
        val ownerId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id
        val project = giveMe.unauthorized().project(ownerId).please()[0]

        // act:
        webTestClient.delete()
            .uri("/api/v1/projects/${project.id}")
            .accept(MediaType.APPLICATION_JSON)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().is5xxServerError
    }

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should delete project not owner`() {
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id
        val ownerId = giveMe.user(UserIdentity.TG.identityKey("353")).please().id
        val project = giveMe.authorized(userId).project(ownerId).please()[0]

        // act:
        webTestClient.delete()
            .uri("/api/v1/projects/${project.id}")
            .accept(MediaType.APPLICATION_JSON)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().is5xxServerError
    }
}
