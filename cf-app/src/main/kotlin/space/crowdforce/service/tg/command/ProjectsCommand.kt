package space.crowdforce.service.tg.command

import org.springframework.stereotype.Service
import space.crowdforce.domain.Project
import space.crowdforce.domain.User
import space.crowdforce.repository.ProjectRepository
import space.crowdforce.service.tg.*
import space.crowdforce.service.tg.Argument.OWNED_PROJECT_ID
import space.crowdforce.service.tg.Navigation.toOuterLink

@Service
class ProjectsCommand(
    private val projectRepository: ProjectRepository
) : Command {

    val requiredArguments = listOf(OWNED_PROJECT_ID)

    override fun execute(user: User, context: UserContext): CommandAnswer {
        val projectId = context.value(OWNED_PROJECT_ID)!!.toInt()

        val project = projectRepository.findById(projectId)

        return CommandAnswer(
                text = projectView(project!!),
                links = listOf(toOuterLink(ProjectsGoalCommand.NAME, listOf(OWNED_PROJECT_ID to projectId)))
        )
    }

    fun projectView(project: Project) : String {
        return """
            Проект № ${project.id}
            Имя: ${project.name}
            Описание: ${project.description}
            
            Выберите доступное действие:            
        """.trimIndent()
    }

    override fun name() = NAME

    override fun arguments(): List<Argument> {
        return requiredArguments
    }

    companion object {
        val NAME = "project"
    }
}
