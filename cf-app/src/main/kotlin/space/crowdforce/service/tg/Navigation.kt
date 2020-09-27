package space.crowdforce.service.tg

import space.crowdforce.service.tg.command.Link
import space.crowdforce.service.tg.command.ProjectsCommand
import space.crowdforce.service.tg.command.ProjectsGoalCommand
import space.crowdforce.service.tg.command.ProjectsGoalListCommand

object Navigation {
    fun mainMenu(): List<Link> {
        return listOf(
                Link(commandName = ProjectsCommand.NAME),
                Link(commandName = ProjectsGoalCommand.NAME),
                Link(commandName = ProjectsGoalListCommand.NAME)
        )
    }

    fun toOuterLink(commandName: String, args: List<Pair<Argument, Any>>): Link {
        return Link(
                commandName = commandName,
                attributes = args.map { it.first.argName to it.second.toString() }
        )
    }

    fun toInnerLink(text: String, args: List<Pair<Argument, Any>>): Link {
        return Link(
                viewName = text,
                attributes = args.map { it.first.argName to it.second.toString() }
        )
    }
}
