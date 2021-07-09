package space.crowdforce.service.tg.command.ti

import org.springframework.stereotype.Service
import space.crowdforce.domain.User
import space.crowdforce.domain.item.ConfirmationStatus
import space.crowdforce.service.tg.Argument
import space.crowdforce.service.tg.UserContext
import space.crowdforce.service.tg.command.Command
import space.crowdforce.service.tg.command.CommandAnswer
import space.crowdforce.service.ti.TrackableItemService

@Service
class ApproveTrackableItemCommand(
    private val trackableItemService: TrackableItemService
) : Command {
    override fun name(): String = NAME

    override fun arguments(): List<Argument> = listOf(Argument.TRACKABLE_ITEM_EVENT_ID)

    override fun execute(user: User, context: UserContext): CommandAnswer {
        val eventId = context.value(Argument.TRACKABLE_ITEM_EVENT_ID)!!.toInt()

        trackableItemService.changeParticipantEventStatus(user.id, eventId, ConfirmationStatus.WAIT_COMPLETING)

        // TODO remove previous message after approve
        return CommandAnswer.finish(
            text = "Спасибо. Будем ждать вас."
        )
    }

    companion object {
        const val NAME = "confirm_participation"
    }
}