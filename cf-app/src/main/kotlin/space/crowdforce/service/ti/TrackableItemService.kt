package space.crowdforce.service.ti

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.item.ConfirmationStatus
import space.crowdforce.domain.item.Period
import space.crowdforce.domain.item.TrackableItem
import space.crowdforce.domain.item.TrackableItemEventPrototype
import space.crowdforce.exception.OperationException
import space.crowdforce.exception.UnauthorizedAccessException
import space.crowdforce.repository.TrackableItemEventParticipantRepository
import space.crowdforce.repository.TrackableItemEventPrototypeRepository
import space.crowdforce.repository.TrackableItemEventRepository
import space.crowdforce.repository.TrackableItemParticipantRepository
import space.crowdforce.repository.TrackableItemRepository
import space.crowdforce.service.activity.ActivityService
import space.crowdforce.service.project.ProjectService
import space.crowdforce.service.tg.Argument
import space.crowdforce.service.tg.CustomTelegramBot
import space.crowdforce.service.tg.command.ti.ApproveTrackableItemCommand
import space.crowdforce.service.tg.command.ti.CompleteTrackableItemCommand
import space.crowdforce.service.tg.command.ti.FailureTrackableItemCommand
import space.crowdforce.service.tg.command.ti.RejectTrackableItemCommand
import space.crowdforce.service.user.UserService
import java.time.Clock
import java.time.LocalDateTime
import java.util.stream.Collectors
import kotlin.streams.toList

@Service
class TrackableItemService(
    private val trackableItemRepository: TrackableItemRepository,
    private val trackableItemParticipantRepository: TrackableItemParticipantRepository,
    private val trackableItemEventPrototypeRepository: TrackableItemEventPrototypeRepository,
    private val trackableItemEventRepository: TrackableItemEventRepository,
    private val trackableItemEventParticipantRepository: TrackableItemEventParticipantRepository,
    private val projectService: ProjectService,
    private val activityService: ActivityService,
    private val clock: Clock,
    private val telegramBot: CustomTelegramBot,
    private val userService: UserService
) {

    companion object {
        private val log = LoggerFactory.getLogger(TrackableItemService::class.java)
    }

    @Transactional
    fun createTrackableItem(userId: Int, projectId: Int, activityId: Int, name: String): TrackableItem {
        check(userId, projectId, activityId)

        return trackableItemRepository.insert(activityId, name)
    }

    @Transactional
    fun updateTrackableItem(userId: Int, projectId: Int, activityId: Int, trackableItemId: Int, name: String) {
        check(userId, projectId, activityId)

        trackableItemRepository.update(activityId, trackableItemId, name)
    }

    @Transactional
    fun getTrackableItems(projectId: Int, activityId: Int): List<TrackableItem> {
        return trackableItemRepository.findAllByActivityId(activityId)
    }

    // TODO add test for it
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    fun createEventsByPrototypes() {
        val currentTime = LocalDateTime.now(clock)

        // We find the last event related with a prototype or an empty result. This knowledge uses to  calculation of
        // next date.
        // TODO add end time of action to this search
        val prototypes = trackableItemEventPrototypeRepository.findAllEventsForCreationByPrototype()
        for (prototype in prototypes) {

            if (Period.NON_RECURRING == prototype.recurring && prototype.lastEventDate != null)
                continue

            val previousEventDate = prototype.lastEventDate ?: prototype.startDate
            val nextEventDate = previousEventDate.plusDays(prototype.recurring.days)

            if (prototype.lastEventDate == null || previousEventDate.isBefore(currentTime) || previousEventDate == currentTime)
                trackableItemEventRepository.insert(
                    prototype.trackableItemId,
                    prototype.message,
                    nextEventDate,
                    prototype.id
                )
        }
    }

    // TODO add test for it
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    fun findCandidatesForWork() {
        val currentTime = LocalDateTime.now(clock)

        // TODO Find not active event for ConfirmationStatus.WAIT_COMPLETING logic
        val activeEvents = trackableItemEventRepository.findAllActiveAtTime(currentTime.minusDays(5))

        for (activeEvent in activeEvents) {
            // In the current implementation, we can only have one wait approval request at the same time.
            val participants = trackableItemEventParticipantRepository.findAllByEventId(activeEvent.id)

            // Checks expiration of wait requests
            val waitRequests = participants.stream().filter {
                it.confirmed == ConfirmationStatus.WAIT_COMPLETING ||
                    it.confirmed == ConfirmationStatus.WAIT_APPROVE
            }.toList()

            if (waitRequests.isNotEmpty()) {
                val waitRequest = waitRequests.first()

                val user = userService.getUserById(waitRequest.userId)

                if (user == null) {
                    // TODO Redesign this logic
                    log.error("User not found [userId${waitRequest.userId}].")

                    trackableItemEventParticipantRepository.updateStatus(
                        activeEvent.id,
                        waitRequest.userId,
                        ConfirmationStatus.APPROVE_AUTO_REJECTED,
                        currentTime
                    )

                    continue
                }

                if (waitRequest.confirmed == ConfirmationStatus.WAIT_APPROVE &&
                    waitRequest.creationTime.plusHours(3).isBefore(currentTime)) { // We waited more then 3 hours.
                    trackableItemEventParticipantRepository.updateStatus(waitRequest.id, ConfirmationStatus.APPROVE_AUTO_REJECTED, currentTime)

                    telegramBot.replaceMsg(user.telegramId.toString(), waitRequest.tgMessageId, "Время подтвреждения вышло." +
                        activeEvent.message + " " + activeEvent.eventTime)
                } else if (waitRequest.confirmed == ConfirmationStatus.WAIT_APPROVE)
                    continue // Will wait additional time

                // Send message about work completion
                if (waitRequest.confirmed == ConfirmationStatus.WAIT_COMPLETING &&
                    waitRequest.lastUpdateTime.isBefore(activeEvent.eventTime) &&
                    currentTime.isAfter(activeEvent.eventTime)
                ) {
                    trackableItemEventParticipantRepository.updateStatus(
                        activeEvent.id,
                        waitRequest.userId,
                        ConfirmationStatus.WAIT_COMPLETING,
                        currentTime // TODO extend statuses for this case
                    )

                    telegramBot.sendMsg(
                        user.telegramId.toString(),
                        "Подтвердите выполнение: " + activeEvent.message + " дата: " + activeEvent.eventTime,
                        listOf(
                            "Принять" to "/${CompleteTrackableItemCommand.NAME} ${Argument.TRACKABLE_ITEM_EVENT_ID.argName}=${activeEvent.id}",
                            "Отказаться" to "/${FailureTrackableItemCommand.NAME} ${Argument.TRACKABLE_ITEM_EVENT_ID.argName}=${activeEvent.id}"
                        )
                    )

                    continue
                } else if (waitRequest.confirmed == ConfirmationStatus.WAIT_COMPLETING)
                    continue // TODO Add auto reject policy
            }

            // Find new potential participants
            val rejectedUserIds = participants.stream().map { it.userId }.collect(Collectors.toSet())

            val availableMembers = trackableItemParticipantRepository.findAllByTrackableItemId(activeEvent.trackableItemId).stream()
                .filter { !rejectedUserIds.contains(it.userId) }.collect(Collectors.toList())

            if (availableMembers.isNotEmpty()) {
                val newParticipant = availableMembers.first()

                val user = userService.getUserById(newParticipant.userId)

                if (user == null) {
                    // TODO Redesign this logic
                    log.error("User not found [userId${newParticipant.userId}].")

                    trackableItemEventParticipantRepository.updateStatus(
                        activeEvent.id,
                        newParticipant.userId,
                        ConfirmationStatus.APPROVE_AUTO_REJECTED,
                        currentTime
                    )

                    continue
                }

                // TODO Extract it to utility class
                val msg = telegramBot.sendMsg(
                    user.telegramId.toString(),
                    "Примите участие в: " + activeEvent.message + " дата: " + activeEvent.eventTime,
                    listOf(
                        "Принять" to "/${ApproveTrackableItemCommand.NAME} ${Argument.TRACKABLE_ITEM_EVENT_ID.argName}=${activeEvent.id}",
                        "Отказаться" to "/${RejectTrackableItemCommand.NAME} ${Argument.TRACKABLE_ITEM_EVENT_ID.argName}=${activeEvent.id}"
                    )
                )

                trackableItemEventParticipantRepository.insert(
                    activeEvent.id,
                    newParticipant.userId,
                    currentTime,
                    ConfirmationStatus.WAIT_APPROVE,
                    msg!!.messageId
                )
            } else {
                // TODO Find participants in the activity or project level.
                log.error("Participants couldn't be found [trackableItemId=${activeEvent.trackableItemId}].")
            }
        }
    }

    private fun check(userId: Int, projectId: Int, activityId: Int) {
        val project = projectService.findProject(projectId)
            ?: throw OperationException("Project [projectId=$projectId] not found.")

        if (project.ownerId != userId)
            throw UnauthorizedAccessException("Invalid project owner [ownerId=$userId].")
    }

    @Transactional
    fun createEventPrototype(
        userId: Int,
        projectId: Int,
        activityId: Int,
        trackableItemId: Int,
        message: String,
        startDate: LocalDateTime,
        recurring: Period
    ): TrackableItemEventPrototype {
        check(userId, projectId, activityId)

        return trackableItemEventPrototypeRepository.insert(trackableItemId, message, startDate, recurring)
    }

    @Transactional
    fun updateEventPrototype(
        userId: Int,
        projectId: Int,
        activityId: Int,
        prototypeEventId: Int,
        message: String,
        startDate: LocalDateTime,
        recurring: Period
    ) {
        check(userId, projectId, activityId)

        return trackableItemEventPrototypeRepository.update(prototypeEventId, message, startDate, recurring)
    }

    @Transactional
    fun deleteTrackableItem(userId: Int, projectId: Int, activityId: Int, trackableItemId: Int) {
        check(userId, projectId, activityId)

        trackableItemEventPrototypeRepository.delete(trackableItemId)
    }

    @Transactional
    fun deleteEventPrototype(userId: Int, projectId: Int, activityId: Int, prototypeEventId: Int) {
        check(userId, projectId, activityId)

        trackableItemEventPrototypeRepository.delete(prototypeEventId)
    }

    @Transactional
    fun getEventPrototypes(trackableItemId: Int): List<TrackableItemEventPrototype> =
        trackableItemEventPrototypeRepository.findAllByTrackableItemId(trackableItemId)

    @Transactional
    fun addParticipant(userId: Int, projectId: Int, activityId: Int, trackableItemId: Int) {
        trackableItemParticipantRepository.insert(trackableItemId, userId)
    }

    @Transactional
    fun deleteParticipant(userId: Int, projectId: Int, activityId: Int, trackableItemId: Int) {
        trackableItemParticipantRepository.delete(trackableItemId, userId)
    }
}