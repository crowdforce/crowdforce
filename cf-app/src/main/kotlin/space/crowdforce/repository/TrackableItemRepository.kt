package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.item.TrackableItem
import space.crowdforce.model.Tables
import space.crowdforce.model.Tables.TRACKABLE_ITEM
import space.crowdforce.model.tables.records.TrackableItemRecord

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class TrackableItemRepository(
    private val dslContext: DSLContext
) {
    companion object {
        val TRACKABLE_ITEM_MAPPER: (TrackableItemRecord) -> TrackableItem = { record: TrackableItemRecord ->
            TrackableItem(
                record.id,
                record.activityId,
                record.name
            )
        }
    }

    fun insert(activityId: Int, name: String) = TRACKABLE_ITEM_MAPPER.invoke(dslContext.insertInto(Tables.TRACKABLE_ITEM)
        .columns(Tables.TRACKABLE_ITEM.ACTIVITY_ID, Tables.TRACKABLE_ITEM.NAME)
        .values(activityId, name)
        .returning()
        .fetchOne())

    fun findAllByActivityId(activityId: Int): List<TrackableItem> =
        dslContext.selectFrom(Tables.TRACKABLE_ITEM)
            .where(Tables.TRACKABLE_ITEM.ACTIVITY_ID.eq(activityId))
            .fetch(TRACKABLE_ITEM_MAPPER)

    fun update(activityId: Int, trackableItemId: Int, name: String) {
        dslContext.update(Tables.TRACKABLE_ITEM)
            .set(Tables.TRACKABLE_ITEM.NAME, name)
            .where(Tables.TRACKABLE_ITEM.ID.eq(trackableItemId))
            .and(Tables.TRACKABLE_ITEM.ACTIVITY_ID.eq(activityId))
            .execute()
    }

    fun delete(trackableItemId: Int) =
        dslContext.delete(TRACKABLE_ITEM)
            .where(TRACKABLE_ITEM.ID.eq(trackableItemId))
            .execute()
}
