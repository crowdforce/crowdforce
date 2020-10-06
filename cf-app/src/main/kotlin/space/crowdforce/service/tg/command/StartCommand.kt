package space.crowdforce.service.tg.command

import org.springframework.stereotype.Service
import space.crowdforce.domain.User
import space.crowdforce.service.tg.Argument
import space.crowdforce.service.tg.Navigation
import space.crowdforce.service.tg.UserContext

@Service
class StartCommand : Command {
    override fun name() = "start"

    override fun arguments(): List<Argument> {
        return emptyList()
    }

    override fun execute(user: User, context: UserContext): CommandAnswer {
        return CommandAnswer(
                text = "Меню",
                links = Navigation.mainMenu()
        )
    }
}
