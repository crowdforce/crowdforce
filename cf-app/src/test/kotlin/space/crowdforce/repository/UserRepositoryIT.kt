package space.crowdforce.repository

import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.AbstractIT
import space.crowdforce.model.Tables
import java.time.LocalDateTime.now

@Transactional
internal class UserRepositoryIT : AbstractIT() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var dslContext: DSLContext

    @BeforeEach
    fun cleanUp() {
        dslContext.truncate(Tables.USERS).cascade().execute()
    }

    @Test
    fun `should insert user when it doesn't exist`() {
        val user = userRepository.insert(234, TEST_TELEGRAM_USER_ID, now())
        assertThat(user.id).isNotNull()
        assertThat(user.telegramId).isEqualTo(234)
    }

    @Test
    fun `should throw exception when user exists`() {
        val user = userRepository.insert(342, TEST_TELEGRAM_USER_ID, now())
        assertThat(user.id).isNotNull()
        assertThat(user.telegramId).isEqualTo(342)

        assertThrows<RuntimeException> {
            userRepository.insert(342, TEST_TELEGRAM_USER_ID, now())
        }
    }

    @Test
    fun `should find user by userName`() {
        val user = userRepository.insert(3232, TEST_TELEGRAM_USER_ID, now())
        assertThat(user.id).isNotNull()
        assertThat(user.telegramId).isEqualTo(3232)

        val byUserName = userRepository.findByTelegramId(3232)
        assertThat(user).isEqualTo(byUserName)
    }
}
