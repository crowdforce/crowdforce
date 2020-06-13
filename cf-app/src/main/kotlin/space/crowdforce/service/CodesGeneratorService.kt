package space.crowdforce.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.repository.UserCodesRepository
import space.crowdforce.tables.records.UserCodesRecord
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

@Service
class CodesGeneratorService(
    private val userCodesRepository: UserCodesRepository
) {

    val secureRandom = SecureRandom().asKotlinRandom()

    @Transactional
    fun generateCode(userId: Int): UserCodesRecord =
        secureRandom.nextInt(100000, 999999)
            .let {
                userCodesRepository.insertNewUserCode(userId, it)
            }

    fun verifyCode(userId: Int, code: Int) =
        userCodesRepository.validateUserCode(userId, code) == 1 &&
            userCodesRepository.deleteUserCode(userId, code) == 1
}
