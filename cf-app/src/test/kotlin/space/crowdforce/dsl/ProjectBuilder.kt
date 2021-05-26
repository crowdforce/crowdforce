package space.crowdforce.dsl

import com.fasterxml.jackson.databind.ObjectMapper
import space.crowdforce.controllers.model.GoalFormUI
import space.crowdforce.controllers.model.Privilege
import space.crowdforce.controllers.model.ProjectUI
import space.crowdforce.domain.geo.Location
import space.crowdforce.service.goal.GoalService
import space.crowdforce.service.project.ProjectService
import space.crowdforce.service.user.UserService
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random.Default.nextDouble

class ProjectBuilder(
    private var authorizedTelegramId: Int?,
    private var ownerTelegramId: Int,
    private var userService: UserService,
    private var projectService: ProjectService,
    private var goalService: GoalService,
    objectMapper: ObjectMapper
) : AbstractBuilder<List<ProjectUI>>(objectMapper) {
    companion object {
        fun randomLocation(): Location = Location(nextDouble(), nextDouble())
    }

    private val counterId = AtomicInteger(1)
    private val subscribers = ArrayList<Int>()
    private val goals = ArrayList<GoalFormUI>()
    private val results = ArrayList<ProjectUI>()

    fun project(ownerName: Int): ProjectBuilder {
        ownerTelegramId = ownerName
        subscribers.clear()

        return this
    }

    fun witSubscriber(telegramId: Int): ProjectBuilder {
        subscribers.add(telegramId)

        return this
    }

    fun withGoal(goalFormUI: GoalFormUI): ProjectBuilder {
        goals.add(goalFormUI)

        return this
    }

    fun and(): ProjectBuilder {
        please()

        return this
    }

    override fun please(): List<ProjectUI> {
        val ownerId = userService.getUserIdByTelegramId(ownerTelegramId)
            ?: throw RuntimeException("Owner not found $ownerTelegramId")

        val id = counterId.getAndIncrement()
        val project = projectService.createProject(ownerId, "test$id", "des$id", randomLocation())

        subscribers.forEach {
            projectService.subscribeUser(userService.getUserIdByTelegramId(it)
                ?: throw RuntimeException("User not found $it"), project.id)
        }

        goals.forEach {
            goalService.addGoal(project.id, project.ownerId, it.name, it.description, it.progress)
        }

        results.add(ProjectUI(
            project.id,
            project.name,
            project.description,
            project.location.longitude,
            project.location.latitude,
            if (authorizedTelegramId != null) subscribers.contains(authorizedTelegramId!!) else false,
            Privilege.READER
        ))

        return results
    }
}