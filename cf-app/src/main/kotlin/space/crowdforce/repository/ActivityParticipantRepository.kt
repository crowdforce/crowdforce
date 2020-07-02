package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.User
import space.crowdforce.model.Tables
import space.crowdforce.model.Tables.ACTIVITY_PARTICIPANTS
import space.crowdforce.model.Tables.USERS

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class ActivityParticipantRepository(
    private val dslContext: DSLContext
) {
    fun insert(userId: Int, activityId: Int) = dslContext.insertInto(ACTIVITY_PARTICIPANTS)
        .columns(ACTIVITY_PARTICIPANTS.USER_ID, ACTIVITY_PARTICIPANTS.ACTIVITY_ID)
        .values(userId, activityId)
        .execute()

    fun findAllByActivityId(activityId: Int): List<User> {
       return dslContext.select()
            .from(ACTIVITY_PARTICIPANTS.join(USERS)
                .on(ACTIVITY_PARTICIPANTS.USER_ID.eq(USERS.ID))
            )
            .where(ACTIVITY_PARTICIPANTS.ACTIVITY_ID.eq(activityId))
            .or(Tables.PROJECT_SUBSCRIBERS.USER_ID.isNull)
            .fetch(UserRepository.USER_MAPPER)
    }

    fun delete(userId: Int, activityId: Int) {
        dslContext.delete(ACTIVITY_PARTICIPANTS)
            .where(ACTIVITY_PARTICIPANTS.ACTIVITY_ID.eq(activityId))
            .and(ACTIVITY_PARTICIPANTS.USER_ID.eq(userId))
            .execute()
    }
}
