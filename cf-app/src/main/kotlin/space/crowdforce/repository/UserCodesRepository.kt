package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.model.Tables.USER_CODES
import java.time.LocalDateTime

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class UserCodesRepository(
    private val dslContext: DSLContext
) {
    fun getActiveUserCode(
        userId: Int,
        expirationCodeTimeSeconds: Long
    ): String? = dslContext.selectFrom(USER_CODES)
        .where(USER_CODES.USER_ID.eq(userId))
        .and(USER_CODES.CREATION_TIME.lessOrEqual(LocalDateTime.now().plusSeconds(expirationCodeTimeSeconds)))
        .fetchAny()?.code

    fun upsertUserCode(
        userId: Int,
        code: String,
        currentTime: LocalDateTime
    ): Int {
        // TODO make upsert
        val userCode = dslContext.selectFrom(USER_CODES)
            .where(USER_CODES.USER_ID.eq(userId))
            .fetchAny()

        return if (userCode != null) {
            dslContext.update(USER_CODES)
                .set(USER_CODES.CREATION_TIME, currentTime)
                .set(USER_CODES.CODE, code)
                .where(USER_CODES.USER_ID.eq(userId))
                .execute()
        } else {
            dslContext.insertInto(USER_CODES, USER_CODES.USER_ID, USER_CODES.CODE, USER_CODES.CREATION_TIME)
                .values(userId, code, currentTime)
                .execute()
        }
    }
}
