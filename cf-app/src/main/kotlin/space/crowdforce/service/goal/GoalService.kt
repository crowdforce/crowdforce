package space.crowdforce.service.goal

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.Goal
import space.crowdforce.exception.OperationException
import space.crowdforce.exception.UnauthorizedAccessException
import space.crowdforce.repository.GoalRepository
import space.crowdforce.service.project.ProjectService
import java.time.LocalDateTime

@Component
class GoalService(
    private val goalRepository: GoalRepository,
    private val projectService: ProjectService
) {
    @Transactional
    fun findGoals(projectId: Int): List<Goal> = goalRepository.findGoals(projectId)

    @Transactional
    fun findGoal(goalId: Int): Goal? = goalRepository.findGoal(goalId)

    @Transactional
    fun addGoal(projectId: Int, userId: Int, name: String, description: String, progress: Int): Goal {
        check(projectId, userId)

        return goalRepository.insert(projectId, name, description, progress, LocalDateTime.now())
    }

    @Transactional
    fun deleteGoal(projectId: Int, goalId: Int, userId: Int): Int {
        check(projectId, goalId, userId)

        return goalRepository.delete(goalId)
    }

    @Transactional
    fun updateGoal(projectId: Int, goalId: Int, userId: Int, name: String, description: String, progress: Int): Int {
        check(projectId, goalId, userId)

        return goalRepository.update(goalId, name, description, progress)
    }

    private fun check(projectId: Int, userId: Int) {
        val project = projectService.findProject(projectId)
            ?: throw OperationException("Project [projectId=$projectId] not found.")

        if (project.ownerId != userId)
            throw UnauthorizedAccessException("Invalid owner [ownerId=$userId].")
    }

    private fun check(projectId: Int, goalId: Int, userId: Int) {
        check(projectId, userId)

        val goal = goalRepository.findGoal(goalId) ?: throw OperationException("Goal [goalId=$goalId] not found.")

        if (goal.projectId != projectId)
            throw OperationException("Bad relation request [projectId=$projectId, goalId=$goalId].")
    }
}
