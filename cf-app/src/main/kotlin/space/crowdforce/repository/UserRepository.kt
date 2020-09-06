package space.crowdforce.repository

import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.User
import space.crowdforce.model.Tables
import space.crowdforce.model.tables.records.UsersRecord
import java.time.LocalDateTime

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class UserRepository(
    private val dslContext: DSLContext
) {

    companion object {
        val USER_MAPPER = { record: Record -> User(record.get("telegram_id") as Int) }
    }

    fun insert(telegramId: Int, name: String, currentTime: LocalDateTime): UsersRecord = dslContext.insertInto(Tables.USERS)
        .columns(Tables.USERS.TG_ID, Tables.USERS.NAME, Tables.USERS.REG_DATE)
        .values(telegramId, name, currentTime)
        .returning()
        .fetchOne()

    fun findByTelegramId(telegramId: Int): UsersRecord? = dslContext.select(Tables.USERS.ID, Tables.USERS.NAME, Tables.USERS.TG_ID, Tables.USERS.REG_DATE)
        .from(Tables.USERS)
        .where(Tables.USERS.TG_ID.eq(telegramId))
        .fetchOneInto(UsersRecord::class.java)
}
