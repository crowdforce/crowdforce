package space.crowdforce.service.tg

import org.springframework.stereotype.Component
import space.crowdforce.domain.Project
import space.crowdforce.domain.User
import space.crowdforce.repository.ProjectRepository
import space.crowdforce.service.tg.Argument.*
import space.crowdforce.service.tg.Navigation.toInnerLink
import space.crowdforce.service.tg.command.CommandAnswer
import space.crowdforce.service.tg.command.Link

interface ArgumentValidator {
    fun validate(user: User, context: UserContext): CommandAnswer?

    fun argument(): Argument
}

@Component
class OwnedProjectId(val projectRepository: ProjectRepository) : ArgumentValidator {
    override fun validate(user: User, context: UserContext): CommandAnswer? {
        val value = context.value(argument())
                ?: return validationFail(user)

        val project = projectRepository.findById(value.toInt())

        if (project == null || project.ownerId != user.id)
            return validationFail(user)

        return null
    }

    private fun validationFail(user: User): CommandAnswer {
        return CommandAnswer.inProgress(
                text = "Выберите один из ваших проектов",
                links = projectList(projectRepository.findOwned(user.id))
        )
    }

    fun projectList(projects: List<Project>): List<Link> {
        return projects
                .map { toInnerLink(it.name, listOf(argument() to it.id.toString())) }
                .toList()
    }

    override fun argument(): Argument = OWNED_PROJECT_ID
}

@Component
class GoalName : ArgumentValidator {
    override fun validate(user: User, context: UserContext): CommandAnswer? {
        val value = context.value(argument())

        if (value == null) {
            val textId = context.pollTextId() ?: return CommandAnswer.inProgress("Введите имя цели")

            context.put(argument(), textId)
        }

        val text: String? = context.valueByLink(argument())

        if (text == null || text.isBlank()) {
            return CommandAnswer.inProgress("Введите имя цели")
        }

        return null
    }

    override fun argument(): Argument = GOAL_NAME
}

@Component
class GoalDescription : ArgumentValidator {
    override fun validate(user: User, context: UserContext): CommandAnswer? {
        val value = context.value(argument())

        if (value == null) {
            val textId = context.pollTextId() ?: return CommandAnswer.inProgress("Введите описание")

            context.put(argument(), textId)
        }

        val text: String? = context.valueByLink(argument())

        if (text == null || text.isBlank()) {
            return CommandAnswer.inProgress("Введите описание")
        }

        return null
    }

    override fun argument(): Argument = GOAL_DESCRIPTION
}

@Component
class ArgumentsValidator(final val argumentValidators: List<ArgumentValidator>) {

    final val map: Map<Argument, ArgumentValidator> = argumentValidators.map { it.argument() to it }.toMap();

    fun validate(user: User, expectedArguments: List<Argument>, context: UserContext): CommandAnswer? {
        return expectedArguments.map { map[it]?.validate(user, context) }.filterNotNull().firstOrNull()
    }
}
