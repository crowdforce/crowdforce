package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import space.crowdforce.Tables
import space.crowdforce.tables.records.UserCodesRecord

@Repository
class UserCodesRepository(
    private val dslContext: DSLContext
) {

    fun insertNewUserCode(userId: Int, code: Int): UserCodesRecord =
        dslContext.insertInto(Tables.USER_CODES)
            .columns(Tables.USER_CODES.USER_ID, Tables.USER_CODES.CODE)
            .values(userId, code)
            .returning()
            .fetchOne()

    fun validateUserCode(userId: Int, code: Int): Int =
        dslContext.selectCount()
            .from(Tables.USER_CODES)
            .where(Tables.USER_CODES.USER_ID.eq(userId))
            .and(Tables.USER_CODES.CODE.eq(code))
            .fetchOne()
            .value1()

    fun deleteUserCode(userId: Int, code: Int): Int =
        dslContext.deleteFrom(Tables.USER_CODES)
            .where(Tables.USER_CODES.USER_ID.eq(userId))
            .and(Tables.USER_CODES.CODE.eq(code))
            .execute()
}
