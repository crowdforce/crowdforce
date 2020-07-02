package space.crowdforce.repository

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.Activity
import space.crowdforce.jooq.geo.PGPoint
import space.crowdforce.model.Tables
import space.crowdforce.model.Tables.ACTIVITIES
import space.crowdforce.model.Tables.ACTIVITY_PARTICIPANTS
import space.crowdforce.model.tables.records.ActivitiesRecord
import java.time.LocalDateTime
import java.util.*

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class ActivityRepository(
    private val dslContext: DSLContext
) {
    companion object {
        val ACTIVITY_MAPPER: (ActivitiesRecord) -> Activity = { record: ActivitiesRecord ->
            Activity(
                record.id,
                record.name,
                record.description,
                record.creationTime,
                record.startTime,
                record.endTime,
                record.projectId,
                false
            )
        }

        val ACTIVITY_WITH_PARTICIPANTS_MAPPER: (Record) -> Activity = { record: Record ->
            Activity(
                record.get("id") as Int,
                record.get("name") as String,
                record.get("description") as String,
                record.get("creation_time") as LocalDateTime,
                record.get("start_time") as LocalDateTime,
                record.get("end_time") as LocalDateTime,
                record.get("project_id") as Int,
                Optional.ofNullable(record.get("user_id")).isPresent
            )
        }
    }

    fun insert(projectId: Int, name: String, description: String, currentTime: LocalDateTime, startTime: LocalDateTime, endTime: LocalDateTime): Activity = ACTIVITY_MAPPER.invoke(
        dslContext.insertInto(ACTIVITIES)
            .columns(ACTIVITIES.PROJECT_ID, ACTIVITIES.NAME, ACTIVITIES.DESCRIPTION, ACTIVITIES.CREATION_TIME, ACTIVITIES.START_TIME, ACTIVITIES.END_TIME)
            .values(projectId, name, description, currentTime, startTime, endTime)
            .returning()
            .fetchOne())

    fun findAll(): List<Activity> =
        dslContext.selectFrom(ACTIVITIES)
            .fetch(ACTIVITY_MAPPER)


    fun findAll(userId: Int): List<Activity> = dslContext.select()
        .from(ACTIVITIES.leftJoin(ACTIVITY_PARTICIPANTS)
            .on(ACTIVITY_PARTICIPANTS.ACTIVITY_ID.eq(ACTIVITIES.ID))
        )
        .where(ACTIVITY_PARTICIPANTS.USER_ID.eq(userId))
        .or(ACTIVITY_PARTICIPANTS.USER_ID.isNull)
        .fetch(ACTIVITY_WITH_PARTICIPANTS_MAPPER)

    fun findAllById(activityId: Int): Activity? =
        dslContext.selectFrom(ACTIVITIES)
            .where(ACTIVITIES.ID.eq(activityId))
            .fetchOne(ACTIVITY_MAPPER)

    fun update(activityId: Int, name: String, description: String, endTime: LocalDateTime, startTime: LocalDateTime) {
        dslContext.update(ACTIVITIES)
            .set(ACTIVITIES.NAME, name)
            .set(ACTIVITIES.DESCRIPTION, description)
            .set(ACTIVITIES.END_TIME, endTime)
            .set(ACTIVITIES.START_TIME, startTime)
            .where(ACTIVITIES.ID.eq(activityId))
            .execute()
    }

    fun delete(activityId: Int) {
        dslContext.delete(ACTIVITIES)
            .where(ACTIVITIES.ID.eq(activityId))
            .execute()
    }
}