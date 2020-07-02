package space.crowdforce.repository

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.Project
import space.crowdforce.domain.User
import space.crowdforce.domain.geo.Location
import space.crowdforce.jooq.geo.PGPoint
import space.crowdforce.model.Tables
import space.crowdforce.model.Tables.PROJECT_SUBSCRIBERS
import space.crowdforce.model.Tables.USERS
import space.crowdforce.model.tables.records.ProjectsRecord
import java.time.LocalDateTime
import java.util.*

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class ProjectSubscriberRepository(
    private val dslContext: DSLContext
) {
    fun insert(userId: Int, projectId: Int) =
        dslContext.insertInto(PROJECT_SUBSCRIBERS)
            .columns(PROJECT_SUBSCRIBERS.USER_ID, PROJECT_SUBSCRIBERS.PROJECT_ID)
            .values(userId, projectId)
            .execute()

    fun findAllByProjectId(projectId: Int): List<User> =
        dslContext.select()
            .from(PROJECT_SUBSCRIBERS.join(USERS)
                .on(PROJECT_SUBSCRIBERS.USER_ID.eq(USERS.ID))
            )
            .where(PROJECT_SUBSCRIBERS.PROJECT_ID.eq(projectId))
            .or(PROJECT_SUBSCRIBERS.USER_ID.isNull)
            .fetch(UserRepository.USER_MAPPER)

    fun delete(userId: Int, projectId: Int) {
        dslContext.delete(PROJECT_SUBSCRIBERS)
            .where(PROJECT_SUBSCRIBERS.USER_ID.eq(userId))
            .and(PROJECT_SUBSCRIBERS.PROJECT_ID.eq(projectId))
            .execute()
    }


}