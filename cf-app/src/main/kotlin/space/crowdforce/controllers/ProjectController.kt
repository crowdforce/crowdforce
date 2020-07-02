package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import space.crowdforce.controllers.model.*
import space.crowdforce.domain.Activity
import space.crowdforce.domain.Project
import space.crowdforce.domain.User
import space.crowdforce.domain.geo.Location
import space.crowdforce.service.activity.ActivityService
import space.crowdforce.service.project.ProjectService
import space.crowdforce.service.user.UserService
import java.lang.RuntimeException
import java.security.Principal


@Api(value = "/api/v1/projects", description = "")
@RestController
@RequestMapping("/api/v1/projects")
class ProjectController(
    private val projectService: ProjectService,
    private val activityService: ActivityService,
    private val userService: UserService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "")
    fun getProjects(principal: Principal?): List<ProjectUI> {
        val userId = if (principal != null) userService.getUserIdByName(principal.name) else null

        return projectService.getAllProjectAggregation(userId).map { map(it) }
    }

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun addProject(
        principal: Principal,
        project: ProjectFormUI
    ) : ProjectUI {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        return map(projectService.createProject(userId, project.name, project.description, Location(project.lng, project.lat)))
    }

    @PutMapping("/{projectId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun updateProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal,
        project: ProjectFormUI
    ) {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        projectService.updateProject(projectId, userId, project.name, project.description, Location(project.lng, project.lat))
    }

    @DeleteMapping("/{projectId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deleteProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal
    ) {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        return projectService.deleteProject(projectId, userId)
    }



    @GetMapping("/{projectId}/subscribers", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getSubscribers(
        @PathVariable("projectId") projectId: Int
    ): List<SubscriberUI> =
        projectService.getSubscribers(projectId).map { map(it) }

    @PutMapping("/{projectId}/subscribers", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun subscribeToProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal
    ) {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        projectService.subscribeUser(projectId, userId)
    }

    @DeleteMapping("/{projectId}/subscribers", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun unsubscribeToProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal
    ) {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        projectService.unsubscribeUser(projectId, userId)
    }

    @GetMapping("/{projectId}/activities", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getActivities(@PathVariable("projectId") projectId: Int, principal: Principal?): List<ActivityUI> {
        val userId = if (principal != null) userService.getUserIdByName(principal.name) else null

        return activityService.getActivities(userId, projectId).map { map(it) }
    }

    @PutMapping("/{projectId}/activities/{activityId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun updateActivity(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal,
        activity: ActivityFormUI
    ) {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        activityService.updateActivity(activityId, userId, activity.name, activity.description, activity.endTime, activity.startTime)
    }

    @DeleteMapping("/{projectId}/activities/{activityId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun deleteActivites(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal
    ) {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        activityService.deleteActivity(activityId, userId)
    }

    @PostMapping("/{projectId}/activities", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun addActivites(
        @PathVariable("projectId") projectId: Int,
        principal: Principal,
        activity: ActivityFormUI
    ) {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        activityService.createActivity(userId, projectId, activity.name, activity.description, activity.startTime, activity.endTime)
    }

    @GetMapping("/{projectId}/activities/{activityId}/participants", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getParticipants(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal?
    ): List<SubscriberUI> {
        return activityService.getParticipants(activityId).map { map(it) }
    }


    @PutMapping("/{projectId}/activities/{activityId}/participants", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun takePartActivity(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal
    ) {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        activityService.takePart(userId, activityId)
    }

    @DeleteMapping("/{projectId}/activities/{activityId}/participants", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun leftActivity(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal
    ) {
        val userId = userService.getUserIdByName(principal.name) ?: throw RuntimeException("Unauthrized")

        activityService.deleteParticipant(userId, activityId)
    }

    fun map(user: User): SubscriberUI =
        SubscriberUI(user.name)

    fun map(activity: Activity): ActivityUI {
        return ActivityUI(
            activity.id,
            activity.name,
            activity.description,
            activity.startDate,
            activity.endDate,
            activity.participant
        )
    }

    fun map(project: Project): ProjectUI {
        return ProjectUI(
            project.id,
            project.name,
            project.description,
            project.location.longitude,
            project.location.latitude,
            project.subscribed
        )
    }
}