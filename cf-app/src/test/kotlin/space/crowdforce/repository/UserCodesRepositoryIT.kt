package space.crowdforce.repository

import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.AbstractIT
import space.crowdforce.dsl.GiveMe
import space.crowdforce.model.Tables
import java.time.LocalDateTime.now
import javax.inject.Inject

@Transactional
class UserCodesRepositoryIT : AbstractIT() {

    @Inject
    lateinit var userCodesRepository: UserCodesRepository

    @Inject
    lateinit var dslContext: DSLContext

    @Inject
    private lateinit var giveMe: GiveMe

    @BeforeEach
    fun cleanUp() {
        dslContext.truncate(Tables.USERS).cascade().execute()
    }

    @Transactional
    @Test
    fun `Should upsert and get user code`() {
        val userId = giveMe.user(TEST_USER).please()
        val code = "12345"
        val currentTime = now()

        userCodesRepository.upsertUserCode(userId, code, currentTime)

        val activeUserCode = userCodesRepository.getActiveUserCode(userId, 100)

        assertThat(activeUserCode).isEqualTo(code)
    }

    @Transactional
    @Test
    fun `Should not return code`() {
        val activeUserCode = userCodesRepository.getActiveUserCode(12345, 100)

        assertThat(activeUserCode).isNull()
    }
}