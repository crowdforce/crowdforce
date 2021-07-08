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
import java.time.Clock
import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class TrackableItemService(
    private val trackableItemRepository: TrackableItemRepository,
    private val trackableItemParticipantRepository: TrackableItemParticipantRepository,
    private val trackableItemEventPrototypeRepository: TrackableItemEventPrototypeRepository,
    private val trackableItemEventRepository: TrackableItemEventRepository,
    private val trackableItemEventParticipantRepository: TrackableItemEventParticipantRepository,
    private val projectService: ProjectService,
    private val activityService: ActivityService,
    private val clock: Clock
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

    /*   @Scheduled(fixedRate = 5 * 60 * 1000)
       fun findCandidatesForWork() {
           //TODO make batching
           val prototypes = trackableItemEventPrototypeRepository.findAll()


           val currentTime = LocalDateTime.now()

           for (prototype in prototypes) {
               if (!(prototype.recurring == Period.TWO_WEEK || prototype.recurring == Period.WEEKLY))
                   continue

               // start date -> current date before 3 days -> date of next notification
               if (prototype.startDate.isBefore(currentTime)) {
                   var nextNotifyTime = prototype.startDate;

                   do {
                       nextNotifyTime = nextNotifyTime.plusDays(prototype.recurring.days)
                   } while (nextNotifyTime.isBefore(currentTime))

                   val firstNotificationDate = nextNotifyTime.minusDays(3).toLocalDate()

                   if (currentTime.toLocalDate().isEqual(firstNotificationDate)) {
                       val unacaptablePrototypes = trackableItemEventPrototypeRepository.findAllUnacaptableEvents()

                       for (unacaptablePrototype in unacaptablePrototypes) {

                           trackableItemEventRepository.insert();

                       }

                       //TODO smart select logic

                   }
               } else {
                   val firstNotificationDate = prototype.startDate.minusDays(3).toLocalDate()

                   if (currentTime.toLocalDate().isEqual(firstNotificationDate)) {

                   }
               }
           }
       }*/

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

    fun findCandidatesForWork() {
        val currentTime = LocalDateTime.now(clock)

        val activeEvents = trackableItemEventRepository.findAllActiveAtTime(currentTime)

        for (activeEvent in activeEvents) {
            val participants = trackableItemEventParticipantRepository.findAllByEventId(activeEvent.id)

            if (participants.isNotEmpty()) {
                if (participants.stream().allMatch { it.confirmed == ConfirmationStatus.WAIT_COMPLETING })
                    continue

                // In the current implementation, we can only have one wait approval request at the same time.
                val waitApproveOpt = participants.stream()
                    .filter { it.confirmed == ConfirmationStatus.WAIT_APPROVE }
                    .findAny()

                if (waitApproveOpt.isPresent) {
                    if (waitApproveOpt.get().creationTime.plusDays(1).isBefore(currentTime)) {
                        trackableItemEventParticipantRepository.updateStatus(
                            waitApproveOpt.get().id,
                            ConfirmationStatus.APPROVE_REJECTED,
                            currentTime
                        )
                        // TODO reject telegram request
                    } else
                        continue
                }
            }

            val rejectedUserIds = participants.stream().map { it.userId }.collect(Collectors.toSet())

            val availableMembers = trackableItemParticipantRepository.findAllByTrackableItemId(activeEvent.trackableItemId).stream()
                .filter { !rejectedUserIds.contains(it.userId) }.collect(Collectors.toList())

            if (availableMembers.isNotEmpty()) {
                val newParticipant = availableMembers.first()

                trackableItemEventParticipantRepository.insert(
                    activeEvent.trackableItemId,
                    newParticipant.userId,
                    currentTime,
                    ConfirmationStatus.WAIT_APPROVE
                )

                // TODO notify tg
            } else {
                /*   // TODO find from other members of same activity

                   activityService.findParticipents(activeEvent.)*/
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