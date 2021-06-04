package space.crowdforce.repository

import org.assertj.core.api.Assertions
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.AbstractIT
import space.crowdforce.domain.Activity
import space.crowdforce.domain.Project
import space.crowdforce.domain.User
import space.crowdforce.model.Tables
import java.time.LocalDateTime
import javax.inject.Inject

@Transactional
internal class ActivityRepositoryIT : AbstractIT() {
    @Inject
    lateinit var activityRepository: ActivityRepository

    @Inject
    lateinit var activityParticipantRepository: ActivityParticipantRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var projectRepository: ProjectRepository

    @Inject
    lateinit var dslContext: DSLContext

    lateinit var project: Project

    lateinit var activity: Activity

    lateinit var user: User

    @BeforeEach
    fun cleanUp() {
        dslContext.truncate(Tables.USERS).cascade().execute()
        dslContext.truncate(Tables.PROJECTS).cascade().execute()

        val currentTime = LocalDateTime.now()

        user = userRepository.insert(TEST_TELEGRAM_USER_ID.toInt(), TEST_TELEGRAM_USER_ID, currentTime)

        project = projectRepository.insert(
            user.id,
            ProjectRepositoryIT.TEST_PROJECT_NAME,
            ProjectRepositoryIT.TEST_DESCRIPTION,
            ProjectRepositoryIT.TEST_LOCATION,
            ProjectRepositoryIT.CURRENT_TIME
        )

        activity = activityRepository.insert(
            project.id,
            "test activity",
            "desc",
            currentTime,
            currentTime,
            currentTime.plusDays(1)
        )
    }

    @Test
    fun `Should return an extended activity with the participant status true`() {
        Assertions.assertThat(activityRepository.findAllByProjectId(project.id, null))
            .isNotEmpty
            .satisfies {
                Assertions.assertThat(it[0].participate).isFalse()
            }

        Assertions.assertThat(activityRepository.findAllByProjectId(project.id, user.id))
            .isNotEmpty
            .satisfies {
                Assertions.assertThat(it[0].participate).isFalse()
            }

        activityParticipantRepository.insert(user.id, activity.id)

        Assertions.assertThat(activityRepository.findAllByProjectId(project.id, user.id))
            .isNotEmpty
            .satisfies {
                Assertions.assertThat(it[0].participate).isTrue()
            }

        Assertions.assertThat(activityRepository.findAllByProjectId(project.id, null))
            .isNotEmpty
            .satisfies {
                Assertions.assertThat(it[0].participate).isFalse()
            }
    }

    @Test
    fun `Should not select isn't existed activity`() {
        Assertions.assertThat(activityRepository.findAllByProjectId(project.id + 1, user.id))
            .isEmpty()

        Assertions.assertThat(activityRepository.findAllByProjectId(project.id + 1, null))
            .isEmpty()
    }
}