package space.crowdforce.service.project

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.Project
import space.crowdforce.domain.User
import space.crowdforce.domain.geo.Location
import space.crowdforce.repository.ProjectRepository
import space.crowdforce.repository.ProjectSubscriberRepository
import java.time.LocalDateTime

@Component
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectSubscriberRepository: ProjectSubscriberRepository
) {
    @Transactional
    fun findProjects(): List<Project> =
        projectRepository.findAll()

    @Transactional
    fun createProject(userId: Int, name: String, description: String, location: Location): Project =
        projectRepository.insert(userId, name, description, location, LocalDateTime.now())

    @Transactional
    fun subscribeUser(userId: Int, projectId: Int) {
        val project = projectRepository.findById(projectId)
            ?: throw RuntimeException("Project not found [projectId: $projectId]")

        projectSubscriberRepository.insert(userId, project.id)
    }

    @Transactional
    fun findProject(projectId: Int): Project? = projectRepository.findById(projectId)

    @Transactional
    fun findProjectAggregation(projectId: Int, userId: Int) = projectRepository.findById(projectId, userId)

    @Transactional
    fun getAllProjectAggregation(userId: Int?): List<Project> = projectRepository.findAll(userId)

    @Transactional
    fun updateProject(projectId: Int, userId: Int, name: String, description: String, location: Location) {
        val project = projectRepository.findById(projectId)
            ?: throw RuntimeException("Project not found [projectId: $projectId]")

        if (project.ownerId != userId)
            throw RuntimeException("Invalid owner id [projectId: $projectId, userId: $userId]")

        projectRepository.update(projectId, name, description, location)
    }

    @Transactional
    fun deleteProject(projectId: Int, userId: Int) {
        val project = projectRepository.findById(projectId)
            ?: throw RuntimeException("Project not found [projectId: $projectId]")

        if (project.ownerId != userId)
            throw RuntimeException("Invalid owner id [projectId: $projectId, userId: $userId]")

        projectRepository.delete(projectId)
    }

    @Transactional
    fun findSubscribers(projectId: Int): List<User> {
        val project = projectRepository.findById(projectId)
            ?: throw RuntimeException("Project not found [projectId: $projectId]")

        return projectSubscriberRepository.findAllByProjectId(project.id)
    }

    @Transactional
    fun unsubscribeUser(projectId: Int, userId: Int) {
        val project = projectRepository.findById(projectId)
            ?: throw RuntimeException("Project not found [projectId: $projectId, userId: $userId]")

        projectSubscriberRepository.delete(userId, project.id)
    }
}
