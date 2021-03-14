package space.crowdforce.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import space.crowdforce.AbstractIT
import space.crowdforce.WithMockUserIdentity
import space.crowdforce.controllers.model.GoalFormUI
import space.crowdforce.domain.UserIdentity
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

    @AfterEach
    internal fun cleanUp() = giveMe.emptyDatabase()

    @Test
    fun `Should get goal`() {
        // given:
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id

        val projectId = giveMe.authorized(userId)
            .project(ownerId = userId).withGoal(GoalFormUI(
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

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should add a project goal`() {
        // given:
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id

        val project = giveMe.authorized(userId).project(userId).please()[0]

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

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should add a project goal not owner`() {

        giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id
        val ownerId = giveMe.user(UserIdentity.TG.identityKey("432")).please().id

        val project = giveMe.authorized(ownerId).project(ownerId).please()[0]

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

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should update a project goal`() {
        // given:
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id

        val project = giveMe.authorized(userId)
            .project(userId)
            .withGoal(GoalFormUI("test1", "test1", 31))
            .please()[0]
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

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should update a project goal not owner`() {
        // given:
        giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id
        val ownerId = giveMe.user(UserIdentity.TG.identityKey("4324")).please().id

        val project = giveMe.authorized(ownerId)
            .project(ownerId)
            .withGoal(GoalFormUI("test1", "test1", 31))
            .please()[0]

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

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should delete a project goal`() {
        // given:
        val userId = giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id

        val project = projectService.findProject(giveMe.authorized(userId).project(userId).please()[0].id)
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

    @WithMockUserIdentity(userIdentity = UserIdentity.TG, userIdentityId = TEST_TELEGRAM_USER_ID, username = "test")
    @Test
    fun `Should delete a project goal not owner`() {
        // given:
        giveMe.user(UserIdentity.TG.identityKey(TEST_TELEGRAM_USER_ID)).please().id
        val ownerId = giveMe.user(UserIdentity.TG.identityKey("4323")).please().id

        val project = projectService.findProject(giveMe.authorized(ownerId).project(ownerId).please()[0].id)
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
