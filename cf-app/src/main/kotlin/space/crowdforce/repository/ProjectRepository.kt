package space.crowdforce.repository

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.Project
import space.crowdforce.domain.geo.Location
import space.crowdforce.jooq.geo.PGPoint
import space.crowdforce.model.Tables.PROJECTS
import space.crowdforce.model.Tables.PROJECT_SUBSCRIBERS
import space.crowdforce.model.tables.records.ProjectsRecord
import java.time.LocalDateTime
import java.util.Optional

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class ProjectRepository(
    private val dslContext: DSLContext
) {
    companion object {
        val PROJECT_MAPPER = { record: ProjectsRecord ->
            Project(
                record.id,
                record.name,
                record.description,
                record.ownerId,
                record.creationTime,
                Location(record.location.x, record.location.y),
                false
            )
        }

        val PROJECT_WITH_SUBSCRIBER_MAPPER = { record: Record ->
            val point = record.get("location") as PGPoint

            Project(
                record.get("id") as Int,
                record.get("name") as String,
                record.get("description") as String,
                record.get("owner_id") as Int,
                record.get("creation_time") as LocalDateTime,
                Location(point.x, point.y),
                Optional.ofNullable(record.get("user_id")).isPresent
            )
        }
    }

    fun insert(
        userId: Int,
        name: String,
        description: String,
        location: Location,
        currentTime: LocalDateTime
    ): Project = PROJECT_MAPPER.invoke(
        dslContext.insertInto(PROJECTS)
            .columns(PROJECTS.OWNER_ID, PROJECTS.NAME, PROJECTS.DESCRIPTION, PROJECTS.CREATION_TIME, PROJECTS.LOCATION)
            .values(userId, name, description, currentTime, PGPoint(location.longitude, location.latitude))
            .returning()
            .fetchOne())

    fun findById(projectId: Int): Project? = dslContext.selectFrom(PROJECTS)
        .where(PROJECTS.ID.eq(projectId))
        .fetchOne(PROJECT_MAPPER)

    fun findById(projectId: Int, userId: Int): Project? = dslContext.select()
        .from(PROJECTS.leftJoin(PROJECT_SUBSCRIBERS)
            .on(PROJECT_SUBSCRIBERS.PROJECT_ID.eq(PROJECTS.ID))
        )
        .where(PROJECT_SUBSCRIBERS.USER_ID.eq(userId))
        .or(PROJECT_SUBSCRIBERS.USER_ID.isNull)
        .and(PROJECTS.ID.eq(projectId))
        .fetchOne(PROJECT_WITH_SUBSCRIBER_MAPPER)

    fun findAll(userId: Int? = null): List<Project> {
        return if (userId == null)
            dslContext.selectFrom(PROJECTS)
                .fetch(PROJECT_MAPPER)
        else
            dslContext.select()
                .from(PROJECTS.leftJoin(PROJECT_SUBSCRIBERS)
                    .on(PROJECT_SUBSCRIBERS.PROJECT_ID.eq(PROJECTS.ID))
                )
                .where(PROJECT_SUBSCRIBERS.USER_ID.eq(userId))
                .or(PROJECT_SUBSCRIBERS.USER_ID.isNull)
                .fetch(PROJECT_WITH_SUBSCRIBER_MAPPER)
    }

    fun findOwned(ownerId: Int): List<Project> =
            dslContext.selectFrom(PROJECTS)
                    .where(PROJECTS.OWNER_ID.eq(ownerId))
                    .fetch(PROJECT_MAPPER)

    fun update(projectId: Int, name: String, description: String, location: Location) = dslContext.update(PROJECTS)
        .set(PROJECTS.NAME, name)
        .set(PROJECTS.DESCRIPTION, description)
        .set(PROJECTS.LOCATION, PGPoint(location.longitude, location.latitude))
        .where(PROJECTS.ID.eq(projectId))
        .execute()

    fun delete(projectId: Int) = dslContext.delete(PROJECTS)
        .where(PROJECTS.ID.eq(projectId))
        .execute()
}
