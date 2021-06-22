package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.item.Period
import space.crowdforce.domain.item.TrackableItemEventPrototype
import space.crowdforce.model.Tables.TRACKABLE_ITEM_EVENT_PROTOTYPE
import space.crowdforce.model.tables.records.TrackableItemEventPrototypeRecord
import java.time.LocalDateTime

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class TrackableItemEventPrototypeRepository(
    private val dslContext: DSLContext
) {
    companion object {
        val TI_EVENT_PROTOTYPE_MAPPER: (TrackableItemEventPrototypeRecord) -> TrackableItemEventPrototype = { record: TrackableItemEventPrototypeRecord ->
            TrackableItemEventPrototype(
                record.id,
                record.message,
                record.startTime,
                Period.valueOf(record.recurring)
            )
        }
    }

    fun insert(trackableItemId: Int, message: String, startDate: LocalDateTime, recurring: Period): TrackableItemEventPrototype =
        TI_EVENT_PROTOTYPE_MAPPER.invoke(dslContext.insertInto(TRACKABLE_ITEM_EVENT_PROTOTYPE)
            .columns(TRACKABLE_ITEM_EVENT_PROTOTYPE.TRACKABLE_ITEM_ID, TRACKABLE_ITEM_EVENT_PROTOTYPE.MESSAGE,
                TRACKABLE_ITEM_EVENT_PROTOTYPE.START_TIME, TRACKABLE_ITEM_EVENT_PROTOTYPE.RECURRING
            )
            .values(trackableItemId, message, startDate, recurring.name)
            .returning()
            .fetchOne())

    fun update(prototypeEventId: Int, message: String, startDate: LocalDateTime, recurring: Period) {
        dslContext.update(TRACKABLE_ITEM_EVENT_PROTOTYPE)
            .set(TRACKABLE_ITEM_EVENT_PROTOTYPE.MESSAGE, message)
            .set(TRACKABLE_ITEM_EVENT_PROTOTYPE.START_TIME, startDate)
            .set(TRACKABLE_ITEM_EVENT_PROTOTYPE.RECURRING, recurring.name)
            .where(TRACKABLE_ITEM_EVENT_PROTOTYPE.ID.eq(prototypeEventId))
            .execute()
    }

    fun delete(trackableItemId: Int) =
        dslContext.delete(TRACKABLE_ITEM_EVENT_PROTOTYPE)
            .where(TRACKABLE_ITEM_EVENT_PROTOTYPE.ID.eq(trackableItemId))
            .execute()

    fun findAllByTrackableItemId(trackableItemId: Int): List<TrackableItemEventPrototype> =
        dslContext.selectFrom(TRACKABLE_ITEM_EVENT_PROTOTYPE)
            .where(TRACKABLE_ITEM_EVENT_PROTOTYPE.TRACKABLE_ITEM_ID.eq(trackableItemId))
            .fetch(TI_EVENT_PROTOTYPE_MAPPER)

    fun findAll(): List<TrackableItemEventPrototype> =
        dslContext.selectFrom(TRACKABLE_ITEM_EVENT_PROTOTYPE)
            .fetch(TI_EVENT_PROTOTYPE_MAPPER)

    fun findAllUnacaptableEvents(): List<TrackableItemEventPrototype> =
        TODO("Not yet implemented")
}
