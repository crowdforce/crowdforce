package space.crowdforce.repository

import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import space.crowdforce.AbstractIT
import space.crowdforce.Tables

internal class UserRepositoryTest : AbstractIT() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var dslContext: DSLContext

    @BeforeEach
    fun cleanUp() {
        dslContext.truncate(Tables.USERS).cascade().execute()
        dslContext.truncate(Tables.USER_CODES).cascade().execute()
    }

    @Test
    fun `should insert user when it doesn't exist`() {
        val user = userRepository.insert("siuwide")
        assertThat(user.id).isNotNull()
        assertThat(user.tgUsername).isEqualTo("siuwide")
    }

    @Test
    fun `should throw exception when user exists`() {
        val user = userRepository.insert("sdnwue")
        assertThat(user.id).isNotNull()
        assertThat(user.tgUsername).isEqualTo("sdnwue")

        assertThrows<RuntimeException> {
            userRepository.insert("sdnwue")
        }
    }

    @Test
    fun `should find user by userName`() {
        val user = userRepository.insert("sfubu")
        assertThat(user.id).isNotNull()
        assertThat(user.tgUsername).isEqualTo("sfubu")

        val byUserName = userRepository.findByUserName("sfubu")
        assertThat(user).isEqualTo(byUserName)
    }
}
