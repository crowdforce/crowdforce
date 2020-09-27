package space.crowdforce.service.tg.command

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import space.crowdforce.domain.User
import space.crowdforce.service.tg.*

@Service
class StartCommand : Command {
    override fun name(): String {
        return "start"
    }

    override fun arguments(): List<Argument> {
        return emptyList()
    }

    override fun execute(user: User, context: UserContext): CommandAnswer {
        return CommandAnswer(
                text = "Меню",
                links = Navigation.mainMenu()
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(StartCommand::class.java)
    }
}
