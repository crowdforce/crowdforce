package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.item.TrackableItemEvent
import space.crowdforce.model.Tables
import space.crowdforce.model.tables.records.TrackableItemEventRecord
import java.time.LocalDateTime
import java.time.LocalTime

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class TrackableItemEventRepository(
    private val dslContext: DSLContext
) {
    companion object {
        val TRACKABLE_ITEM_EVENT_MAPPER: (TrackableItemEventRecord) -> TrackableItemEvent = { record: TrackableItemEventRecord ->
            TrackableItemEvent(
                record.id,
                record.message,
                record.trackableItemId,
                record.eventTime,
                record.participantsNumber
            )
        }
    }

    fun insert(trackableItemId: Int, message: String, currentTime: LocalDateTime, trackablePrototypeId: Int) = TRACKABLE_ITEM_EVENT_MAPPER
        .invoke(dslContext.insertInto(Tables.TRACKABLE_ITEM_EVENT)
            .columns(Tables.TRACKABLE_ITEM_EVENT.MESSAGE, Tables.TRACKABLE_ITEM_EVENT.TRACKABLE_ITEM_ID,
                Tables.TRACKABLE_ITEM_EVENT.EVENT_TIME, Tables.TRACKABLE_ITEM_EVENT.TRACKABLE_ITEM_EVENT_PROTOTYPE_ID,
                Tables.TRACKABLE_ITEM_EVENT.PARTICIPANTS_NUMBER
            )
            .values(message, trackableItemId, currentTime, trackablePrototypeId, 1)
            .returning()
            .fetchOne()
        )

    fun findAllActiveAtTime(currentTime: LocalDateTime): List<TrackableItemEvent> = dslContext.selectFrom(Tables.TRACKABLE_ITEM_EVENT)
        .where(Tables.TRACKABLE_ITEM_EVENT.EVENT_TIME.ge(LocalDateTime.of(currentTime.toLocalDate(), LocalTime.MIDNIGHT)))
        .fetch(TRACKABLE_ITEM_EVENT_MAPPER)

    fun findById(eventId: Int): TrackableItemEvent? = dslContext.selectFrom(Tables.TRACKABLE_ITEM_EVENT)
        .where(Tables.TRACKABLE_ITEM_EVENT.ID.eq(eventId))
        .fetchOne(TRACKABLE_ITEM_EVENT_MAPPER)
}
