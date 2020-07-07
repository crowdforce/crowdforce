package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import space.crowdforce.controllers.model.*
import space.crowdforce.domain.Activity
import space.crowdforce.domain.Project
import space.crowdforce.domain.User
import space.crowdforce.domain.geo.Location
import space.crowdforce.service.activity.ActivityService
import space.crowdforce.service.project.ProjectService
import space.crowdforce.service.user.UserService
import java.security.Principal

@Api(value = "/api/v1/projects", description = "")
@RestController
@RequestMapping("/api/v1/projects", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
class ProjectController(
    private val projectService: ProjectService,
    private val activityService: ActivityService,
    private val userService: UserService
) {
    @GetMapping
    @ApiOperation(value = "")
    fun getProjects(principal: Principal?): List<ProjectUI> {
        val userId = principal?.let { userService.getUserIdByName(principal.name) }

        return projectService.getAllProjectAggregation(userId).map { map(it) }
    }

    @PostMapping
    suspend fun addProject(
        principal: Principal?,
        @RequestBody project: ProjectFormUI
    ): ProjectUI {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

        return map(projectService.createProject(userId, project.name, project.description, Location(project.lng, project.lat)))
    }

    @PutMapping("/{projectId}")
    suspend fun updateProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?,
        @RequestBody project: ProjectFormUI
    ) {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

        projectService.updateProject(projectId, userId, project.name, project.description, Location(project.lng, project.lat))
    }

    @DeleteMapping("/{projectId}")
    suspend fun deleteProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

        projectService.deleteProject(projectId, userId)
    }

    @GetMapping("/{projectId}/subscribers")
    suspend fun getSubscribers(
        @PathVariable("projectId") projectId: Int
    ): List<SubscriberUI> =
        projectService.findSubscribers(projectId).map { map(it) }

    @PutMapping("/{projectId}/subscribers")
    suspend fun subscribeToProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

        projectService.subscribeUser(projectId, userId)
    }

    @DeleteMapping("/{projectId}/subscribers")
    suspend fun unsubscribeToProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

        projectService.unsubscribeUser(projectId, userId)
    }

    @GetMapping("/{projectId}/activities")
    fun getActivities(@PathVariable("projectId") projectId: Int, principal: Principal?): List<ActivityUI> {
        val userId = principal?.let { userService.getUserIdByName(it.name) }

        return activityService.findActivities(userId, projectId).map { map(it) }
    }

    @PutMapping("/{projectId}/activities/{activityId}")
    suspend fun updateActivity(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal?,
        @RequestBody activity: ActivityFormUI
    ) {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

        activityService.updateActivity(activityId, userId, activity.name, activity.description, activity.endTime, activity.startTime)
    }

    @DeleteMapping("/{projectId}/activities/{activityId}")
    suspend fun deleteActivites(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

        activityService.deleteActivity(activityId, userId)
    }

    @PostMapping("/{projectId}/activities")
    suspend fun addActivites(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?,
        @RequestBody activity: ActivityFormUI
    ) {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

        activityService.createActivity(userId, projectId, activity.name, activity.description, activity.startTime, activity.endTime)
    }

    @GetMapping("/{projectId}/activities/{activityId}/participants")
    suspend fun getParticipants(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int
    ): List<SubscriberUI> {
        return activityService.getParticipants(activityId).map { map(it) }
    }


    @PutMapping("/{projectId}/activities/{activityId}/participants")
    suspend fun takePartActivity(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

        activityService.takePart(userId, activityId)
    }

    @DeleteMapping("/{projectId}/activities/{activityId}/participants")
    suspend fun leftActivity(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByName(it.name) } ?: throw RuntimeException("Unauthorized")

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
            activity.participate
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