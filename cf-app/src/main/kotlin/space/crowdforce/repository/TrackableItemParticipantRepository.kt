package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.item.TrackableItemParticipant
import space.crowdforce.model.Tables.TRACKABLE_ITEM_PARTICIPANTS
import space.crowdforce.model.tables.records.TrackableItemParticipantsRecord

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class TrackableItemParticipantRepository(
    private val dslContext: DSLContext
) {
    companion object {
        val TRACKABLE_ITEM_PARTICIPANT_MAPPER: (TrackableItemParticipantsRecord) -> TrackableItemParticipant = { record: TrackableItemParticipantsRecord ->
            TrackableItemParticipant(
                record.trackableItemId,
                record.userId
            )
        }
    }

    fun findAllByTrackableItemId(trackableItemId: Int): List<TrackableItemParticipant> {
        return dslContext.selectFrom(TRACKABLE_ITEM_PARTICIPANTS)
            .where(TRACKABLE_ITEM_PARTICIPANTS.TRACKABLE_ITEM_ID.eq(trackableItemId))
            .fetch(TRACKABLE_ITEM_PARTICIPANT_MAPPER)
    }

    fun insert(trackableItemId: Int, userId: Int): TrackableItemParticipant {
        return TRACKABLE_ITEM_PARTICIPANT_MAPPER.invoke(dslContext.insertInto(TRACKABLE_ITEM_PARTICIPANTS)
            .columns(TRACKABLE_ITEM_PARTICIPANTS.TRACKABLE_ITEM_ID, TRACKABLE_ITEM_PARTICIPANTS.USER_ID)
            .values(trackableItemId, userId)
            .returning()
            .fetchOne())
    }

    fun delete(trackableItemId: Int, userId: Int) {
        dslContext.delete(TRACKABLE_ITEM_PARTICIPANTS)
            .where(TRACKABLE_ITEM_PARTICIPANTS.TRACKABLE_ITEM_ID.eq(trackableItemId))
            .and(TRACKABLE_ITEM_PARTICIPANTS.USER_ID.eq(userId))
    }
}