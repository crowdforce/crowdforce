package space.crowdforce.repository

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL.max
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.item.Period
import space.crowdforce.domain.item.TrackableItemEventPrototype
import space.crowdforce.domain.item.TrackableItemEventPrototypeLast
import space.crowdforce.model.Tables.TRACKABLE_ITEM_EVENT
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
                Period.valueOf(record.recurring),
                record.trackableItemId,
                record.participantsNumber
            )
        }

        val TI_EVENT_PROTOTYPE_LAST_MAPPER = { record: Record ->
            TrackableItemEventPrototypeLast(
                record.get("id") as Int,
                record.get("message") as String,
                record.get("start_time") as LocalDateTime,
                Period.valueOf(record.get("recurring") as String),
                record.get("event_time") as LocalDateTime?,
                record.get("trackable_item_id") as Int
            )
        }
    }

    fun insert(trackableItemId: Int, message: String, startDate: LocalDateTime, recurring: Period): TrackableItemEventPrototype =
        TI_EVENT_PROTOTYPE_MAPPER.invoke(dslContext.insertInto(TRACKABLE_ITEM_EVENT_PROTOTYPE)
            .columns(TRACKABLE_ITEM_EVENT_PROTOTYPE.TRACKABLE_ITEM_ID, TRACKABLE_ITEM_EVENT_PROTOTYPE.MESSAGE,
                TRACKABLE_ITEM_EVENT_PROTOTYPE.START_TIME, TRACKABLE_ITEM_EVENT_PROTOTYPE.RECURRING,
                TRACKABLE_ITEM_EVENT_PROTOTYPE.PARTICIPANTS_NUMBER
            )
            .values(trackableItemId, message, startDate, recurring.name, 1)
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

    fun findAllEventsForCreationByPrototype(): List<TrackableItemEventPrototypeLast> {
        return dslContext.select(
            TRACKABLE_ITEM_EVENT_PROTOTYPE.asterisk(),
            max(TRACKABLE_ITEM_EVENT.EVENT_TIME).`as`("event_time")
        )
            .from(TRACKABLE_ITEM_EVENT_PROTOTYPE
                .leftJoin(TRACKABLE_ITEM_EVENT).on(TRACKABLE_ITEM_EVENT_PROTOTYPE.ID
                    .eq(TRACKABLE_ITEM_EVENT.TRACKABLE_ITEM_EVENT_PROTOTYPE_ID))
            )
            .groupBy(TRACKABLE_ITEM_EVENT_PROTOTYPE.ID)
            .fetch(TI_EVENT_PROTOTYPE_LAST_MAPPER)
    }
}
