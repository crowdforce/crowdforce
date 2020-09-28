package space.crowdforce.service.tg.command

import org.springframework.stereotype.Service
import space.crowdforce.domain.Goal
import space.crowdforce.domain.User
import space.crowdforce.repository.GoalRepository
import space.crowdforce.service.tg.*

@Service
class ProjectsGoalListCommand(
    private val goalRepository: GoalRepository
) : Command {

    val requiredArguments = listOf(Argument.OWNED_PROJECT_ID)

    override fun execute(user: User, context: UserContext): CommandAnswer {
        val projectId = context.value(Argument.OWNED_PROJECT_ID)!!.toInt()

        return CommandAnswer.finish(
                text = goalListView(goalRepository.findGoals(projectId)),
                links = Navigation.mainMenu()
        )
    }

    fun goalListView(goal: List<Goal>) : String {
        return goal.map {
            "Цель : ${it.name} | Прогресс : ${it.progressBar} | Описание : ${it.description}"
        }.joinToString(separator="\n")
    }

    override fun name() = NAME

    override fun arguments(): List<Argument> {
        return requiredArguments
    }

    companion object {
        val NAME = "GoalList"
    }
}
