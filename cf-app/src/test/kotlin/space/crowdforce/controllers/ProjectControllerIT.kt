package space.crowdforce.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
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

    @Inject
    private lateinit var mapper: com.fasterxml.jackson.databind.ObjectMapper

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
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectHeader().valueEquals("Content-Type", "application/json")
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
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectHeader().valueEquals("Content-Type", "application/json")
            .expectBody().json(expected)
    }

    @Test
    fun `Should add a project unauthorized user`() {
        // given:
        giveMe.user(TEST_USER).please()

        val projectForm = ProjectFormUI("test", "test", 123.123, 321.321)

        // act and check:
        webTestClient.post()
            .uri("/api/v1/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(projectForm), ProjectFormUI::class.java)
            .exchange()
            .expectStatus().isCreated
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
            .expectStatus().isCreated
    }


    @Test
    fun `Should update project fields unauthorized user`() {
        // given:

        // act:

        // check:
    }

    @WithMockUser(username = TEST_USER)
    @Test
    fun `Should update project fields not owner`() {
        // given:

        // act:

        // check:
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
            .exchange()
            .expectStatus().isAccepted

        assertThat(projectService.getProject(project.id))
            .isNull()
    }

    @Test
    fun `Should delete project unauthorized user`() {
        // given:

        // act:

        // check:
    }

    @WithMockUser(username = TEST_USER)
    @Test
    fun `Should delete project not owner`() {
        // given:

        // act:

        // check:
    }

    //TODO unautorized or not owner

/*    fun `Should return list of subscribers`() {

    }

    @Test
    fun `Should subscibe user to project`() {

    }

    @Test
    fun `Should unsubscibe user from project`() {

    }

    @Test
    fun `Should return project activities`() {

    }

    @Test
    fun `Should update activity`() {

    }*/

    //TODO unautorized or not owner


}