package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import space.crowdforce.controllers.model.ProjectUI
import space.crowdforce.controllers.model.SubscriberUI
import space.crowdforce.controllers.model.ProjectFormUI
import space.crowdforce.controllers.model.ActivityUI
import space.crowdforce.controllers.model.ActivityFormUI
import space.crowdforce.controllers.model.GoalUI
import space.crowdforce.controllers.model.GoalFormUI
import space.crowdforce.domain.Activity
import space.crowdforce.domain.Project
import space.crowdforce.domain.User
import space.crowdforce.domain.geo.Location
import space.crowdforce.exception.ResourceNotFoundException
import space.crowdforce.exception.UnauthorizedAccessException
import space.crowdforce.service.activity.ActivityService
import space.crowdforce.service.goal.GoalService
import space.crowdforce.service.mapper.MapperService
import space.crowdforce.service.project.ProjectService
import space.crowdforce.service.user.UserService
import java.security.Principal

@Api(value = "/api/v1/projects", description = "")
@RestController
@RequestMapping("/api/v1/projects", consumes = [MediaType.ALL_VALUE], produces = [APPLICATION_JSON_VALUE])
class ProjectController(
    private val projectService: ProjectService,
    private val activityService: ActivityService,
    private val userService: UserService,
    private val goalService: GoalService,
    private val mapperService: MapperService
) {
    @GetMapping
    @ApiOperation(value = "")
    fun getProjects(principal: Principal?): List<ProjectUI> {
        val userId = principal?.let { userService.getUserIdByTelegramId(principal.name.toInt()) }

        return projectService.getAllProjectAggregation(userId).map { map(it) }
    }

    @PostMapping
    suspend fun addProject(
        principal: Principal?,
        @RequestBody project: ProjectFormUI
    ): ProjectUI {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

        return map(projectService.createProject(userId, project.name, project.description, Location(project.lng, project.lat)))
    }

    @GetMapping("/{projectId}")
    suspend fun getProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?
    ): ProjectUI = map(if (principal == null) {
        projectService.findProject(projectId) ?: throw ResourceNotFoundException()
    } else {
        val userId = userService.getUserIdByTelegramId(principal.name.toInt()) ?: throw UnauthorizedAccessException()

        projectService.findProjectAggregation(projectId, userId) ?: throw ResourceNotFoundException()
    })

    @PutMapping("/{projectId}")
    suspend fun updateProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?,
        @RequestBody project: ProjectFormUI
    ) {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

        projectService.updateProject(projectId, userId, project.name, project.description, Location(project.lng, project.lat))
    }

    @DeleteMapping("/{projectId}")
    suspend fun deleteProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

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
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

        projectService.subscribeUser(projectId, userId)
    }

    @DeleteMapping("/{projectId}/subscribers")
    suspend fun unsubscribeToProject(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

        projectService.unsubscribeUser(projectId, userId)
    }

    @GetMapping("/{projectId}/activities")
    fun getActivities(@PathVariable("projectId") projectId: Int, principal: Principal?): List<ActivityUI> {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }

        return activityService.findActivities(userId, projectId).map { map(it) }
    }

    @PutMapping("/{projectId}/activities/{activityId}")
    suspend fun updateActivity(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal?,
        @RequestBody activity: ActivityFormUI
    ) {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

        activityService.updateActivity(activityId, userId, activity.name, activity.description, activity.endTime, activity.startTime)
    }

    @DeleteMapping("/{projectId}/activities/{activityId}")
    suspend fun deleteActivites(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

        activityService.deleteActivity(activityId, userId)
    }

    @PostMapping("/{projectId}/activities")
    suspend fun addActivites(
        @PathVariable("projectId") projectId: Int,
        principal: Principal?,
        @RequestBody activity: ActivityFormUI
    ) {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

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
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

        activityService.takePart(userId, activityId)
    }

    @DeleteMapping("/{projectId}/activities/{activityId}/participants")
    suspend fun leftActivity(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("activityId") activityId: Int,
        principal: Principal?
    ) {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw RuntimeException("Unauthorized")

        activityService.deleteParticipant(userId, activityId)
    }

    @GetMapping("/{projectId}/goals")
    suspend fun getGoals(@PathVariable("projectId") projectId: Int): List<GoalUI> = goalService.findGoals(projectId)
        .map { mapperService.map(it) }

    @GetMapping("/{projectId}/goals/{goalId}")
    suspend fun getGoal(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("goalId") goalId: Int
    ): ResponseEntity<GoalUI> = goalService.findGoal(goalId)?.let { ResponseEntity.ok().body(mapperService.map(it)) }
        ?: ResponseEntity.notFound().build()

    @PutMapping("/{projectId}/goals/{goalId}")
    suspend fun updateGoals(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("goalId") goalId: Int,
        @RequestBody goalFormUI: GoalFormUI,
        principal: Principal?
    ): ResponseEntity<Any> {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw UnauthorizedAccessException()

        goalService.updateGoal(projectId, goalId, userId, goalFormUI.name, goalFormUI.description, goalFormUI.progress)

        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{projectId}/goals/{goalId}")
    suspend fun deleteGoals(
        @PathVariable("projectId") projectId: Int,
        @PathVariable("goalId") goalId: Int,
        principal: Principal?
    ): ResponseEntity<Any> {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw UnauthorizedAccessException()

        goalService.deleteGoal(projectId, goalId, userId)

        return ResponseEntity.ok().build()
    }

    @PostMapping("/{projectId}/goals")
    suspend fun addGoals(
        @PathVariable("projectId") projectId: Int,
        @RequestBody goalFormUI: GoalFormUI,
        principal: Principal?
    ): ResponseEntity<Any> {
        val userId = principal?.let { userService.getUserIdByTelegramId(it.name.toInt()) }
            ?: throw UnauthorizedAccessException()

        val goal = mapperService.map(goalService.addGoal(projectId, userId, goalFormUI.name, goalFormUI.description, goalFormUI.progress))

        return ResponseEntity.ok().body(goal)
    }

    fun map(user: User): SubscriberUI =
        SubscriberUI(user.telegramId)

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
