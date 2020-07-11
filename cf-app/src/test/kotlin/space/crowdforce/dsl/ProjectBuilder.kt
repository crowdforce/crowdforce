package space.crowdforce.dsl

import com.fasterxml.jackson.databind.ObjectMapper
import space.crowdforce.controllers.model.ProjectUI
import space.crowdforce.domain.geo.Location
import space.crowdforce.service.project.ProjectService
import space.crowdforce.service.user.UserService
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random.Default.nextDouble

class ProjectBuilder(
    private var authorizedUserName: String?,
    private var ownerUserName: String,
    private var userService: UserService,
    private var projectService: ProjectService,
    objectMapper: ObjectMapper
) : AbstractBuilder<List<ProjectUI>>(objectMapper) {
    companion object {
        fun randomLocation(): Location = Location(nextDouble(), nextDouble())
    }

    private val counterId = AtomicInteger(1)
    private val subscribers = ArrayList<String>()
    private val results = ArrayList<ProjectUI>()

    fun project(ownerName: String): ProjectBuilder {
        ownerUserName = ownerName
        subscribers.clear()

        return this
    }

    fun witSubscriber(userName: String): ProjectBuilder {
        subscribers.add(userName)

        return this
    }

    fun and(): ProjectBuilder {
        please()

        return this
    }

    override fun please(): List<ProjectUI> {
        val ownerId = userService.getUserIdByName(ownerUserName)
            ?: throw RuntimeException("Owner not found $ownerUserName")

        val id = counterId.getAndIncrement()
        val project = projectService.createProject(ownerId, "test$id", "des$id", randomLocation())

        subscribers.forEach {
            projectService.subscribeUser(userService.getUserIdByName(it)
                ?: throw RuntimeException("User not found $it"), project.id)
        }

        results.add(ProjectUI(
            project.id,
            project.name,
            project.description,
            project.location.longitude,
            project.location.latitude,
            if (authorizedUserName != null) subscribers.contains(authorizedUserName!!) else false
        ))

        return results
    }
}