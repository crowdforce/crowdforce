package space.crowdforce.repository

import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.AbstractIT
import space.crowdforce.domain.item.Period
import space.crowdforce.domain.item.TrackableItem
import space.crowdforce.model.Tables
import java.time.LocalDateTime
import javax.inject.Inject

@Transactional
internal class TrackableItemEventPrototypeRepositoryTest : AbstractIT() {
    @Inject
    private lateinit var userRepository: UserRepository

    @Inject
    private lateinit var projectRepository: ProjectRepository

    @Inject
    private lateinit var trackableItemEventRepository: TrackableItemEventRepository

    @Inject
    private lateinit var trackableItemEventPrototypeRepository: TrackableItemEventPrototypeRepository

    @Inject
    private lateinit var trackableItemRepository: TrackableItemRepository

    @Inject
    private lateinit var activityRepository: ActivityRepository

    @Inject
    lateinit var dslContext: DSLContext

    lateinit var currentTime: LocalDateTime

    lateinit var ti: TrackableItem

    @BeforeEach
    fun cleanUp() {
        dslContext.truncate(Tables.USERS).cascade().execute()
        dslContext.truncate(Tables.PROJECTS).cascade().execute()
        dslContext.truncate(Tables.ACTIVITIES).cascade().execute()
        dslContext.truncate(Tables.TRACKABLE_ITEM).cascade().execute()

        currentTime = LocalDateTime.now()

        val user = userRepository.insert(TEST_TELEGRAM_USER_ID.toInt(), TEST_TELEGRAM_USER_ID, currentTime)

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

        ti = trackableItemRepository.insert(activity.id, "test")
    }

    @Test
    fun `Should return list of prototypes for creation`() {
        val nonRecurringPrototype = trackableItemEventPrototypeRepository.insert(ti.id, Period.NON_RECURRING.name, currentTime, Period.NON_RECURRING)

        val lastDate = currentTime.plusDays(2 * Period.TWO_WEEK.days)
        val twoWeekPrototype = trackableItemEventPrototypeRepository.insert(ti.id, Period.TWO_WEEK.name, currentTime, Period.TWO_WEEK)
        trackableItemEventRepository.insert(twoWeekPrototype.trackableItemId, twoWeekPrototype.message, currentTime, twoWeekPrototype.id)
        trackableItemEventRepository.insert(twoWeekPrototype.trackableItemId, twoWeekPrototype.message, lastDate, twoWeekPrototype.id)

        val prototypes = trackableItemEventPrototypeRepository.findAllEventsForCreationByPrototype()

        assertThat(prototypes)
            .hasSize(2)
            .anyMatch { it.lastEventDate == null && it.message == nonRecurringPrototype.message }
            .anyMatch { lastDate.isEqual(it.lastEventDate) && it.message == twoWeekPrototype.message }
    }

    @Test
    fun `Should return last time of event when there are more than one`() {
        val prototype = trackableItemEventPrototypeRepository.insert(ti.id, "test message", currentTime, Period.TWO_WEEK)
        val lastDate = currentTime.plusDays(2 * Period.TWO_WEEK.days)

        trackableItemEventRepository.insert(prototype.trackableItemId, prototype.message, currentTime, prototype.id)
        trackableItemEventRepository.insert(prototype.trackableItemId, prototype.message, currentTime.plusDays(Period.TWO_WEEK.days), prototype.id)
        trackableItemEventRepository.insert(prototype.trackableItemId, prototype.message, lastDate, prototype.id)

        assertThat(trackableItemEventPrototypeRepository.findAllEventsForCreationByPrototype())
            .hasSize(1)
            .anyMatch { lastDate.isEqual(it.lastEventDate) }
    }

    @Test
    fun `Should return null last time if event is not created`() {
        trackableItemEventPrototypeRepository.insert(ti.id, "test message", currentTime, Period.NON_RECURRING)

        assertThat(trackableItemEventPrototypeRepository.findAllEventsForCreationByPrototype())
            .hasSize(1)
            .anyMatch { it.lastEventDate == null }
    }
}