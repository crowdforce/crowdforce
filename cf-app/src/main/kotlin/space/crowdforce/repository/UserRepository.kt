package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import space.crowdforce.Tables
import space.crowdforce.tables.records.UsersRecord

@Repository
class UserRepository(
    private val dslContext: DSLContext
) {

    fun insert(userName: String): UsersRecord =
        dslContext.insertInto(Tables.USERS)
            .columns(Tables.USERS.TG_USERNAME)
            .values(userName)
            .returning()
            .fetchOne()

    fun findByUserName(userName: String): UsersRecord? =
        dslContext.select(Tables.USERS.ID, Tables.USERS.TG_USERNAME, Tables.USERS.REG_DATE)
            .from(Tables.USERS)
            .where(Tables.USERS.TG_USERNAME.eq(userName))
            .fetchOneInto(UsersRecord::class.java)

}
