package space.crowdforce.dao

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.AbstractIT
import space.crowdforce.entities.SampleEntity
import javax.inject.Inject

@Transactional
internal class SampleDaoImplIT : AbstractIT() {
    @Inject
    private lateinit var sampleDao: SampleDao

    @Test
    fun `Test find non existent entity`() {
        assertThat(sampleDao.findById(1))
            .isNull()
    }

    @Test
    fun `Test insert and find`() {
        // given:
        val entity = SampleEntity(
            name = "test"
        )

        // when:
        val persistedEntity = sampleDao.insert(entity)

        // then:
        assertThat(persistedEntity.id)
            .isGreaterThan(0)
        assertThat(sampleDao.findById(persistedEntity.id))
            .isNotNull
            .satisfies {
                assertThat(it!!.name)
                    .isEqualTo(entity.name)
            }
    }
}