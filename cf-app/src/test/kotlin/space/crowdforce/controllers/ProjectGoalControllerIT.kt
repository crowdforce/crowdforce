package space.crowdforce.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import space.crowdforce.AbstractIT
import space.crowdforce.controllers.model.GoalFormUI
import space.crowdforce.dsl.GiveMe
import space.crowdforce.service.goal.GoalService
import space.crowdforce.service.project.ProjectService
import javax.inject.Inject

class ProjectGoalControllerIT : AbstractIT() {
    @Inject
    private lateinit var webTestClient: WebTestClient

    @Inject
    private lateinit var giveMe: GiveMe

    @Inject
    private lateinit var goalService: GoalService

    @Inject
    private lateinit var projectService: ProjectService

    @BeforeEach
    internal fun setUp() = giveMe.emptyDatabase()

    @Test
    fun `Should get goal`() {
        // given:
        giveMe.user(TEST_TELEGRAM_USER_ID.toInt()).please()

        val projectId = giveMe.authorized(TEST_TELEGRAM_USER_ID.toInt())
            .project(ownerTelegramId = TEST_TELEGRAM_USER_ID.toInt()).withGoal(GoalFormUI(
                "test goal 1",
                "test description 1",
                20
            )).withGoal(GoalFormUI(
                "test goal 2",
                "test description 2",
                50
            )).please()[0].id

        // act and check:
        webTestClient.get()
            .uri("/api/v1/projects/$projectId/goals")
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody().json(giveMe.json(goalService.findGoals(projectId)))
    }

    @WithMockUser(username = TEST_TELEGRAM_USER_ID)
    @Test
    fun `Should add a project goal`() {
        // given:
        giveMe.user(TEST_TELEGRAM_USER_ID.toInt()).please()

        val project = giveMe.authorized(TEST_TELEGRAM_USER_ID.toInt()).project(TEST_TELEGRAM_USER_ID.toInt()).please()[0]

        val updatedGoal = GoalFormUI("test3", "test2", 66)

        // act and check:
        webTestClient.post()
            .uri("/api/v1/projects/${project.id}/goals")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGoal)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.name").isEqualTo(updatedGoal.name)
            .jsonPath("$.description").isEqualTo(updatedGoal.description)
            .jsonPath("$.progress").isEqualTo(updatedGoal.progress)
    }

    @WithMockUser(username = TEST_TELEGRAM_USER_ID)
    @Test
    fun `Should add a project goal not owner`() {
        // given:
        val userName = 432
        giveMe.user(TEST_TELEGRAM_USER_ID.toInt()).please()
        giveMe.user(userName).please()

        val project = giveMe.authorized(userName).project(userName).please()[0]

        val updatedGoal = GoalFormUI("test3", "test2", 66)

        // act and check:
        webTestClient.post()
            .uri("/api/v1/projects/${project.id}/goals")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGoal)
            .exchange()
            .expectStatus().isForbidden
    }

    @WithMockUser(username = TEST_TELEGRAM_USER_ID)
    @Test
    fun `Should update a project goal`() {
        // given:
        giveMe.user(TEST_TELEGRAM_USER_ID.toInt()).please()

        val project = giveMe.authorized(TEST_TELEGRAM_USER_ID.toInt()).project(TEST_TELEGRAM_USER_ID.toInt()).withGoal(GoalFormUI("test1", "test1", 31)).please()[0]
        val goal = goalService.findGoals(project.id)[0]

        val updatedGoal = GoalFormUI("test2", "test2", 55)

        // act and check:
        webTestClient.put()
            .uri("/api/v1/projects/${project.id}/goals/${goal.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGoal)
            .exchange()
            .expectStatus().isOk

        assertThat(goalService.findGoal(goal.id)).satisfies {
            assertThat(it!!.name).isEqualTo(updatedGoal.name)
            assertThat(it.description).isEqualTo(updatedGoal.description)
            assertThat(it.progressBar).isEqualTo(updatedGoal.progress)
        }
    }

    @WithMockUser(username = TEST_TELEGRAM_USER_ID)
    @Test
    fun `Should update a project goal not owner`() {
        // given:
        val userName = 4324
        giveMe.user(TEST_TELEGRAM_USER_ID.toInt()).please()
        giveMe.user(userName).please()

        val project = giveMe.authorized(userName).project(userName).withGoal(GoalFormUI("test1", "test1", 31)).please()[0]
        val goal = goalService.findGoals(project.id)[0]

        val updatedGoal = GoalFormUI("test2", "test2", 55)

        // act and check:
        webTestClient.put()
            .uri("/api/v1/projects/${project.id}/goals/${goal.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGoal)
            .exchange()
            .expectStatus().isForbidden
    }

    @WithMockUser(username = TEST_TELEGRAM_USER_ID)
    @Test
    fun `Should delete a project goal`() {
        // given:
        giveMe.user(TEST_TELEGRAM_USER_ID.toInt()).please()

        val project = projectService.findProject(giveMe.authorized(TEST_TELEGRAM_USER_ID.toInt()).project(TEST_TELEGRAM_USER_ID.toInt()).please()[0].id)
            ?: throw RuntimeException("Not found")
        val goal = goalService.addGoal(project.id, project.ownerId, "test", "test", 55)

        // act and check:
        webTestClient.delete()
            .uri("/api/v1/projects/${project.id}/goals/${goal.id}")
            .accept(MediaType.APPLICATION_JSON)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk

        assertThat(goalService.findGoal(goal.id))
            .isNull()
    }

    @WithMockUser(username = TEST_TELEGRAM_USER_ID)
    @Test
    fun `Should delete a project goal not owner`() {
        // given:
        val userName = 4323
        giveMe.user(TEST_TELEGRAM_USER_ID.toInt()).please()
        giveMe.user(userName).please()

        val project = projectService.findProject(giveMe.authorized(userName).project(userName).please()[0].id)
            ?: throw RuntimeException("Not found")
        val goal = goalService.addGoal(project.id, project.ownerId, "test", "test", 55)

        // act:
        webTestClient.delete()
            .uri("/api/v1/projects/${project.id}/goals/${goal.id}")
            .accept(MediaType.APPLICATION_JSON)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isForbidden
    }
}
