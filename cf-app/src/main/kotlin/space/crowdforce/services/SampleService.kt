package space.crowdforce.services

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.dao.SampleDao
import space.crowdforce.entities.SampleEntity

@Service
class SampleService(private val sampleDao: SampleDao) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun findSampleEntity(id: Long) = sampleDao.findById(id)

    @Transactional
    fun createSampleEntity(name: String): SampleEntity {
        val entity = sampleDao.insert(
            SampleEntity(
                name = name
            )
        )

        logger.info("Entity $entity has been created")
        return entity
    }
}
