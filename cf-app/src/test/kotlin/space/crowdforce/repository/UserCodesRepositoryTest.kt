package space.crowdforce.repository

import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import space.crowdforce.AbstractIT
import space.crowdforce.Tables

internal class UserCodesRepositoryTest : AbstractIT() {

    @Autowired
    lateinit var userCodesRepository: UserCodesRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var dslContext: DSLContext

    @AfterEach
    fun cleanUp() {
        dslContext.truncate(Tables.USERS).cascade().execute()
        dslContext.truncate(Tables.USER_CODES).cascade().execute()
    }

    @Test
    fun `should insert into codes when doesn't exist`() {
        val user = userRepository.insert("userName")

        val code = 10000
        val userCode = userCodesRepository.insertNewUserCode(user.id, code)
        assertThat(userCode.userId).isEqualTo(user.id)
        assertThat(userCode.code).isEqualTo(code)
    }

    @Test
    fun `should fail on insert code when exists`() {
        val user = userRepository.insert("userName")

        val code = 10000
        val userCode = userCodesRepository.insertNewUserCode(user.id, code)
        assertThat(userCode.userId).isEqualTo(user.id)
        assertThat(userCode.code).isEqualTo(code)

        assertThrows<RuntimeException> {
            userCodesRepository.insertNewUserCode(user.id, code)
        }
    }

    @Test
    fun `should validate user code`() {
        val user = userRepository.insert("userName")

        val code = 10000
        val userCode = userCodesRepository.insertNewUserCode(user.id, code)
        assertThat(userCode.userId).isEqualTo(user.id)
        assertThat(userCode.code).isEqualTo(code)

        assertThat(userCodesRepository.validateUserCode(user.id, code))
            .isEqualTo(1)
    }

    @Test
    fun `code doesn't exist check`() {
        val user = userRepository.insert("userName")

        val code = 10000
        assertThat(userCodesRepository.validateUserCode(user.id, code))
            .isEqualTo(0)
    }

    @Test
    fun `should delete code from db`() {
        val user = userRepository.insert("userName")

        val code = 10000
        val userCode = userCodesRepository.insertNewUserCode(user.id, code)
        assertThat(userCode.userId).isEqualTo(user.id)
        assertThat(userCode.code).isEqualTo(code)

        assertThat(userCodesRepository.validateUserCode(user.id, code))
            .isEqualTo(1)

        assertThat(userCodesRepository.deleteUserCode(user.id, code))
            .isEqualTo(1)

        assertThat(userCodesRepository.validateUserCode(user.id, code))
            .isEqualTo(0)

        assertThat(userCodesRepository.deleteUserCode(user.id, code))
            .isEqualTo(0)
    }
}
