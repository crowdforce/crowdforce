package space.crowdforce.repository

import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.AbstractIT
import space.crowdforce.domain.geo.Location
import space.crowdforce.model.Tables
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import javax.inject.Inject

@Transactional
internal class ProjectRepositoryIT : AbstractIT() {
    companion object {
        const val TEST_PROJECT_NAME = "project name"
        const val TEST_DESCRIPTION = "description"
        val CURRENT_TIME: LocalDateTime = now()
        val TEST_LOCATION = Location(123.233, 123.23)
    }

    @Inject
    private lateinit var userRepository: UserRepository

    @Inject
    private lateinit var projectRepository: ProjectRepository

    @Inject
    lateinit var dslContext: DSLContext

    @BeforeEach
    fun cleanUp() {
        dslContext.truncate(Tables.USERS).cascade().execute()
        dslContext.truncate(Tables.PROJECTS).cascade().execute()
    }

    @Test
    fun `Should save project`() {
        val user = userRepository.insert(TEST_TELEGRAM_USER_ID.toInt(), TEST_TELEGRAM_USER_ID, now())

        val project = projectRepository.insert(
            user.id,
            TEST_PROJECT_NAME,
            TEST_DESCRIPTION,
            TEST_LOCATION,
            CURRENT_TIME
        )

        assertThat(projectRepository.findById(project.id))
            .isNotNull
            .satisfies {
                assertThat(it?.id).isEqualTo(project.id)
                assertThat(it?.name).isEqualTo(TEST_PROJECT_NAME)
                assertThat(it?.ownerId).isEqualTo(user.id)
                assertThat(it?.description).isEqualTo(TEST_DESCRIPTION)
                assertThat(it?.creationTime).isEqualTo(CURRENT_TIME)
                assertThat(it?.location).isEqualTo(TEST_LOCATION)
            }
    }
}