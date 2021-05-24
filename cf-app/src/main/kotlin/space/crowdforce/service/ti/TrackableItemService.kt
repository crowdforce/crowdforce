package space.crowdforce.service.ti

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.item.Period
import space.crowdforce.domain.item.TrackableItem
import space.crowdforce.domain.item.TrackableItemEventPrototype
import space.crowdforce.exception.OperationException
import space.crowdforce.exception.UnauthorizedAccessException
import space.crowdforce.repository.TrackableItemEventPrototypeRepository
import space.crowdforce.repository.TrackableItemEventRepository
import space.crowdforce.repository.TrackableItemRepository
import space.crowdforce.service.activity.ActivityService
import space.crowdforce.service.project.ProjectService
import java.time.LocalDateTime

@Service
class TrackableItemService(
    private val trackableItemRepository: TrackableItemRepository,
    private val trackableItemEventPrototypeRepository: TrackableItemEventPrototypeRepository,
    private val trackableItemEventRepository: TrackableItemEventRepository,
    private val projectService: ProjectService,
    private val activityService: ActivityService
) {

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

    // @Scheduled()
    /*fun findCandidatesForWork() {
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

    private fun check(userId: Int, projectId: Int, activityId: Int) {
        val project = projectService.findProject(projectId)
            ?: throw OperationException("Project [projectId=$projectId] not found.")

        if (project.ownerId != userId)
            throw UnauthorizedAccessException("Invalid project owner [ownerId=$userId].")

        val activity = activityService.findActivity(activityId)
            ?: throw OperationException("Activity [activityId=$activityId] not found.")

        if (project.ownerId != activity.projectId)
            throw UnauthorizedAccessException("Invalid activity owner [ownerId=$userId].")
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
}