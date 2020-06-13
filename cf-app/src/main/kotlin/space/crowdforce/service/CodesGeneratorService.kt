package space.crowdforce.service

import org.springframework.stereotype.Service
import space.crowdforce.repository.UserCodesRepository
import space.crowdforce.tables.records.UserCodesRecord
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

@Service
class CodesGeneratorService(
    private val userCodesRepository: UserCodesRepository
) {

    val secureRandom = SecureRandom().asKotlinRandom()

    fun generateCode(userId: Int): UserCodesRecord =
        secureRandom.nextInt(100000, 999999)
            .let {
                userCodesRepository.insertNewUserCode(userId, it)
            }
}
