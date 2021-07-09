package space.crowdforce.service.tg.command.ti

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.User
import space.crowdforce.domain.item.ConfirmationStatus
import space.crowdforce.repository.TrackableItemEventParticipantRepository
import space.crowdforce.service.tg.Argument
import space.crowdforce.service.tg.UserContext
import space.crowdforce.service.tg.command.Command
import space.crowdforce.service.tg.command.CommandAnswer
import java.time.Clock
import java.time.LocalDateTime

@Service
class CompleteTrackableItemCommand(
    private val trackableItemEventParticipantRepository: TrackableItemEventParticipantRepository,
    private val clock: Clock
) : Command {
    override fun name(): String = NAME

    override fun arguments(): List<Argument> = listOf(Argument.TRACKABLE_ITEM_EVENT_ID)

    @Transactional
    override fun execute(user: User, context: UserContext): CommandAnswer {
        val eventId = context.value(Argument.TRACKABLE_ITEM_EVENT_ID)!!.toInt()

        trackableItemEventParticipantRepository.updateStatus(eventId, user.id, ConfirmationStatus.COMPLETED, LocalDateTime.now(clock))

        // TODO remove previous message after approve
        return CommandAnswer.finish(
            text = "Спасибо. Задача готова.",
            replace = true
        )
    }

    companion object {
        const val NAME = "work_complete"
    }
}