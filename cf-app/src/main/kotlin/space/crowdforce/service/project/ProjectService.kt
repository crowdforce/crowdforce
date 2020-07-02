package space.crowdforce.service.project

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.controllers.model.ProjectUI
import space.crowdforce.domain.Project
import space.crowdforce.domain.User
import space.crowdforce.domain.geo.Location
import space.crowdforce.repository.ActivityParticipantRepository
import space.crowdforce.repository.ActivityRepository
import space.crowdforce.repository.ProjectRepository
import space.crowdforce.repository.ProjectSubscriberRepository
import java.time.LocalDateTime


@Component
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectSubscriberRepository: ProjectSubscriberRepository,
    private val activityRepository: ActivityRepository,
    private val activityParticipantRepository: ActivityParticipantRepository
) {
    @Transactional
    fun getProjects(): List<Project> =
        projectRepository.findAll()

    @Transactional
    fun createProject(userId: Int, name: String, description: String, location: Location): Project =
        projectRepository.insert(userId, name, description, location, LocalDateTime.now())

    @Transactional
    fun subscribeUser(userId: Int, projectId: Int) {
        val project = projectRepository.findById(projectId) ?: throw RuntimeException("Project not found")

        projectSubscriberRepository.insert(userId, project.id)
    }

    @Transactional
    fun getProject(projectId: Int): Project? = projectRepository.findById(projectId)

    @Transactional
    fun getAllProjectAggregation(userId: Int?): List<Project> {
        return if(userId != null)
            projectRepository.findAll(userId)
        else
            projectRepository.findAll()
    }

    @Transactional
    fun updateProject(projectId : Int, userId: Int, name: String, description: String, location: Location) {
        val project = projectRepository.findById(projectId) ?: throw RuntimeException("Project not found")

        if(project.ownerId != userId)
            throw RuntimeException("Invallid owner id")

        projectRepository.update(projectId, name, description, location)
    }

    @Transactional
    fun deleteProject(projectId: Int, userId: Int) {
        val project = projectRepository.findById(projectId) ?: throw RuntimeException("Project not found")

        if(project.ownerId != userId)
            throw RuntimeException("Invallid owner id")

        projectRepository.delete(projectId)
    }

    @Transactional
    fun getSubscribers(projectId: Int): List<User> {
        val project = projectRepository.findById(projectId) ?: throw RuntimeException("Project not found")

        return projectSubscriberRepository.findAllByProjectId(project.id)
    }

    @Transactional
    fun unsubscribeUser(projectId: Int, userId: Int) {
        val project = projectRepository.findById(projectId) ?: throw RuntimeException("Project not found")

        projectSubscriberRepository.delete(userId, project.id)
    }
}