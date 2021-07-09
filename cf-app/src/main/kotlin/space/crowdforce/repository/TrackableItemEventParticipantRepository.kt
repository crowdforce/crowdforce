package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.item.ConfirmationStatus
import space.crowdforce.domain.item.TrackableItemEventParticipant
import space.crowdforce.model.Tables.TRACKABLE_ITEM_EVENT_PARTICIPANTS
import space.crowdforce.model.tables.records.TrackableItemEventParticipantsRecord
import java.time.LocalDateTime

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class TrackableItemEventParticipantRepository(
    private val dslContext: DSLContext
) {

    companion object {
        val TRACKABLE_ITEM_EVENT_PARTICIPANT_MAPPER = { record: TrackableItemEventParticipantsRecord ->
            TrackableItemEventParticipant(
                record.id,
                record.trackableItemEventId,
                record.userId,
                record.creationTime,
                record.lastUpdateTime,
                ConfirmationStatus.value(record.confirmed)
            )
        }
    }

    fun findAllByEventId(eventId: Int): List<TrackableItemEventParticipant> {
        return dslContext.selectFrom(TRACKABLE_ITEM_EVENT_PARTICIPANTS)
            .where(TRACKABLE_ITEM_EVENT_PARTICIPANTS.TRACKABLE_ITEM_EVENT_ID.eq(eventId))
            .and(TRACKABLE_ITEM_EVENT_PARTICIPANTS.CONFIRMED.notEqual(ConfirmationStatus.COMPLETED.code)
                .or(TRACKABLE_ITEM_EVENT_PARTICIPANTS.CONFIRMED.notEqual(ConfirmationStatus.COMPLETING_REJECTED.code))
            )
            .fetch(TRACKABLE_ITEM_EVENT_PARTICIPANT_MAPPER)
    }

    fun updateStatus(id: Int, confirmationStatus: ConfirmationStatus, currentTime: LocalDateTime) {
        dslContext.update(TRACKABLE_ITEM_EVENT_PARTICIPANTS)
            .set(TRACKABLE_ITEM_EVENT_PARTICIPANTS.CONFIRMED, confirmationStatus.code)
            .set(TRACKABLE_ITEM_EVENT_PARTICIPANTS.LAST_UPDATE_TIME, currentTime)
            .where(TRACKABLE_ITEM_EVENT_PARTICIPANTS.ID.eq(id))
    }

    fun updateStatus(id: Int, userId: Int, confirmationStatus: ConfirmationStatus, currentTime: LocalDateTime) {
        dslContext.update(TRACKABLE_ITEM_EVENT_PARTICIPANTS)
            .set(TRACKABLE_ITEM_EVENT_PARTICIPANTS.CONFIRMED, confirmationStatus.code)
            .set(TRACKABLE_ITEM_EVENT_PARTICIPANTS.LAST_UPDATE_TIME, currentTime)
            .where(TRACKABLE_ITEM_EVENT_PARTICIPANTS.USER_ID.eq(userId).and(TRACKABLE_ITEM_EVENT_PARTICIPANTS.ID.eq(id)))
    }

    fun insert(
        trackableItemEventId: Int,
        userId: Int,
        creationTime: LocalDateTime,
        confirmed: ConfirmationStatus
    ): TrackableItemEventParticipant {
        return TRACKABLE_ITEM_EVENT_PARTICIPANT_MAPPER.invoke(
            dslContext.insertInto(TRACKABLE_ITEM_EVENT_PARTICIPANTS)
                .columns(TRACKABLE_ITEM_EVENT_PARTICIPANTS.CONFIRMED, TRACKABLE_ITEM_EVENT_PARTICIPANTS.TRACKABLE_ITEM_EVENT_ID,
                    TRACKABLE_ITEM_EVENT_PARTICIPANTS.CREATION_TIME, TRACKABLE_ITEM_EVENT_PARTICIPANTS.LAST_UPDATE_TIME,
                    TRACKABLE_ITEM_EVENT_PARTICIPANTS.USER_ID
                ).values(confirmed.code, trackableItemEventId, creationTime, creationTime, userId)
                .returning()
                .fetchOne())
    }
}