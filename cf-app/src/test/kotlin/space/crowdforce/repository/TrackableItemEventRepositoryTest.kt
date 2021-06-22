package space.crowdforce.repository

import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.AbstractIT
import space.crowdforce.model.Tables
import java.time.LocalDateTime
import javax.inject.Inject

@Transactional
internal class TrackableItemEventRepositoryTest : AbstractIT() {
    @Inject
    private lateinit var userRepository: UserRepository

    @Inject
    private lateinit var projectRepository: ProjectRepository

    @Inject
    private lateinit var trackableItemEventRepository: TrackableItemEventRepository

    @Inject
    private lateinit var activityRepository: ActivityRepository

    @Inject
    lateinit var dslContext: DSLContext


    @BeforeEach
    fun cleanUp() {
        dslContext.truncate(Tables.USERS).cascade().execute()
        dslContext.truncate(Tables.PROJECTS).cascade().execute()
    }

    @Test
    fun `Should return only active event`() {
        val currentTime = LocalDateTime.now()
        val user = userRepository.insert(TEST_TELEGRAM_USER_ID.toInt(), TEST_TELEGRAM_USER_ID, currentTime)

        //TODO create DSL
        val project = projectRepository.insert(
            user.id,
            ProjectRepositoryIT.TEST_PROJECT_NAME,
            ProjectRepositoryIT.TEST_DESCRIPTION,
            ProjectRepositoryIT.TEST_LOCATION,
            ProjectRepositoryIT.CURRENT_TIME
        )

        val activity = activityRepository.insert(
            project.id,
            "test",
            "test",
            currentTime,
            currentTime,
            currentTime.plusDays(1)
        )

       /* trackableItemEventRepository.insert(

        )

        trackableItemEventRepository.findAllActiveAtTime()*/
    }
}