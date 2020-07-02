package space.crowdforce.repository

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.User
import space.crowdforce.model.Tables
import space.crowdforce.model.tables.records.UsersRecord

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class UserRepository(
    private val dslContext: DSLContext
) {

    companion object {
        val USER_MAPPER = { record: Record -> User(record.get("name") as String) }
    }

    fun insert(userName: String): UsersRecord = dslContext.insertInto(Tables.USERS)
        .columns(Tables.USERS.TG_USERNAME)
        .values(userName)
        .returning()
        .fetchOne()

    fun findByUserName(userName: String): UsersRecord? = dslContext.select(Tables.USERS.ID, Tables.USERS.TG_USERNAME, Tables.USERS.REG_DATE)
        .from(Tables.USERS)
        .where(Tables.USERS.TG_USERNAME.eq(userName))
        .fetchOneInto(UsersRecord::class.java)
}
