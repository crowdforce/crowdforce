package space.crowdforce.service.tg.command

import space.crowdforce.domain.User
import space.crowdforce.service.tg.Argument
import space.crowdforce.service.tg.UserContext

interface Command {
    fun name(): String

    fun arguments(): List<Argument>

    fun execute(user: User, context: UserContext): CommandAnswer
}

enum class AnswerStatus {
    IN_PROGRESS,
    FINISH
}

data class CommandAnswer(
    val status: AnswerStatus = AnswerStatus.FINISH,
    val text: String,
    val links: List<Link> = emptyList(),
    val replace: Boolean = false
) {
    companion object {
        fun inProgress(text: String, links: List<Link> = emptyList(), replace: Boolean = false) =
            CommandAnswer(AnswerStatus.IN_PROGRESS, text, links, replace)

        fun finish(text: String, links: List<Link> = emptyList(), replace: Boolean = false) =
            CommandAnswer(AnswerStatus.FINISH, text, links, replace)
    }
}

data class Link(
    val viewName: String? = null,
    val commandName: String? = null,
    val attributes: List<Pair<String, String>> = emptyList()
)

data class Response(
    val text: String,
    val actions: List<Pair<String, String>> = emptyList(),
    val replace: Boolean = false
)
