package space.crowdforce.service

import org.springframework.stereotype.Service
import space.crowdforce.repository.UserCodesRepository
import space.crowdforce.tables.records.UserCodesRecord
import java.nio.ByteBuffer
import java.security.SecureRandom

@Service
class CodesGeneratorService(
    private val userCodesRepository: UserCodesRepository
) {

    val secureRandom = SecureRandom()

    fun generateCode(userId: Int): UserCodesRecord =
        secureRandom.generateSeed(6)
            .let { ByteBuffer.wrap(it).int }
            .let {
                userCodesRepository.insertNewUserCode(userId, it)
            }

}
