package space.crowdforce.repository

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.User
import space.crowdforce.domain.UserIdentityKey
import space.crowdforce.model.Tables
import java.time.LocalDateTime

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class UserRepository(
    private val dslContext: DSLContext
) {

    companion object {
        val USER_MAPPER = { record: Record ->
            User(
                record.get("id") as Int,
                record.get("name") as String
            )
        }
    }

    fun insert(identityKey: UserIdentityKey, name: String, currentTime: LocalDateTime): User {
        val user = USER_MAPPER.invoke(
            dslContext.insertInto(Tables.USERS)
                .columns(Tables.USERS.NAME, Tables.USERS.REG_DATE)
                .values(name, currentTime)
                .returning()
                .fetchOne()
        )

        dslContext.insertInto(Tables.USER_IDENTITIES)
            .columns(
                Tables.USER_IDENTITIES.IDENTITY_TYPE,
                Tables.USER_IDENTITIES.IDENTITY_ID,
                Tables.USER_IDENTITIES.USER_ID
            )
            .values(identityKey.identity.identityType, identityKey.identityId, user.id)
            .execute()
        return user
    }

    fun findByUserId(userId: Int): User? = dslContext.select(Tables.USERS.ID, Tables.USERS.NAME, Tables.USERS.REG_DATE)
        .from(Tables.USERS)
        .where(Tables.USERS.ID.eq(userId))
        .fetchOne(USER_MAPPER)

    fun findByIdentityKey(userIdentityKey: UserIdentityKey): User? = dslContext.select()
        .from(
            Tables.USERS.leftJoin(Tables.USER_IDENTITIES)
                .on(Tables.USER_IDENTITIES.USER_ID.eq(Tables.USERS.ID))
        )
        .where(Tables.USER_IDENTITIES.IDENTITY_TYPE.eq(userIdentityKey.identity.identityType))
        .and(Tables.USER_IDENTITIES.IDENTITY_ID.eq(userIdentityKey.identityId))
        .fetchOne(USER_MAPPER)
}
