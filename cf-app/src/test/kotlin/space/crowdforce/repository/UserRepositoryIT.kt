package space.crowdforce.repository

import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.AbstractIT
import space.crowdforce.domain.UserIdentity
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
        val userIdentityKey = UserIdentity.TG.identityKey("123")
        val user = userRepository.insert(userIdentityKey, "234", now())
        assertThat(user.id).isNotNull()
        assertThat(user.name).isEqualTo("234")
    }

    @Test
    fun `should throw exception when user exists for the identity key`() {
        val userIdentityKey = UserIdentity.TG.identityKey("123")
        val user = userRepository.insert(userIdentityKey, "342", now())
        assertThat(user.id).isNotNull()
        assertThat(user.name).isEqualTo("342")

        assertThrows<RuntimeException> {
            userRepository.insert(userIdentityKey, "342", now())
        }
    }

    @Test
    fun `should find user by id`() {
        val userIdentityKey = UserIdentity.TG.identityKey("123")
        val user = userRepository.insert(userIdentityKey, "3232", now())
        assertThat(user.id).isNotNull()
        assertThat(user.name).isEqualTo("3232")

        val byUserName = userRepository.findByUserId(user.id)
        assertThat(user).isEqualTo(byUserName)
    }

    @Test
    fun `should find user by identity key`() {
        val userIdentityKey = UserIdentity.TG.identityKey("123")
        val user = userRepository.insert(userIdentityKey, "3232", now())

        val byIdentity = userRepository.findByIdentityKey(UserIdentity.TG.identityKey("123"))
        assertThat(byIdentity).isEqualTo(user)

        assertThat(userRepository.findByIdentityKey(UserIdentity.ANON.identityKey("123"))).isNull()
        assertThat(userRepository.findByIdentityKey(UserIdentity.TG.identityKey("789"))).isNull()
    }
}
