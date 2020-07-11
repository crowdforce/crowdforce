package space.crowdforce.service.activity

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.Activity
import space.crowdforce.domain.User
import space.crowdforce.repository.ActivityParticipantRepository
import space.crowdforce.repository.ActivityRepository
import space.crowdforce.service.project.ProjectService
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Component
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val participantRepository: ActivityParticipantRepository,
    private val projectService: ProjectService
) {
    @Transactional
    fun createActivity(
        userId: Int,
        projectId: Int,
        name: String,
        description: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Activity {
        val project = projectService.findProject(projectId)
            ?: throw RuntimeException("Invalid projectId [projectId: $projectId]")

        if (project.ownerId != userId)
            throw RuntimeException("Current user [userId: $userId] doesn't have access")

        return activityRepository.insert(projectId, name, description, now(), startTime, endTime)
    }

    @Transactional
    fun takePart(userId: Int, activityId: Int) = participantRepository.insert(userId, activityId)

    @Transactional
    fun findActivities(userId: Int?, projectId: Int): List<Activity> = activityRepository.findAllByProjectId(projectId, userId)

    @Transactional
    fun updateActivity(activityId: Int, userId: Int, name: String, description: String, endTime: LocalDateTime, startTime: LocalDateTime) {
        val activity = activityRepository.findAllById(activityId)
            ?: throw RuntimeException("Invalid activityId [activityId: $activityId, userId: $userId]")

        val project = projectService.findProject(activity.projectId)
            ?: throw RuntimeException("Invalid projectId [projectId: ${activity.projectId}, userId: $userId]")

        if (project.ownerId != userId)
            throw RuntimeException("Current user [userId: $userId] hasn't access")

        activityRepository.update(activityId, name, description, endTime, startTime)
    }

    @Transactional
    fun deleteActivity(activityId: Int, userId: Int) {
        val activity = activityRepository.findAllById(activityId)
            ?: throw RuntimeException("Invalid activityId [activityId: $activityId, userId: $userId]")

        val project = projectService.findProject(activity.projectId)
            ?: throw RuntimeException("Invalid projectId [projectId: ${activity.projectId}, userId: $userId]")

        if (project.ownerId != userId)
            throw RuntimeException("Current user [userId: $userId] hasn't access")

        activityRepository.delete(activityId)
    }

    @Transactional
    fun getParticipants(activityId: Int): List<User> {
        val activity = activityRepository.findAllById(activityId)
            ?: throw RuntimeException("Invalid activityId [activityId: $activityId]")

        return participantRepository.findAllByActivityId(activity.id)
    }

    @Transactional
    fun deleteParticipant(userId: Int, activityId: Int) {
        participantRepository.delete(userId, activityId)
    }
}
