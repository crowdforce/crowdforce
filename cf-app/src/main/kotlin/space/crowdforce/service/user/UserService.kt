package space.crowdforce.service.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.User
import space.crowdforce.repository.UserRepository
import java.time.LocalDateTime.now

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun getOrCreateUser(telegramId: Int, name: String): User {
        return userRepository.findByTelegramId(telegramId) ?: userRepository.insert(telegramId, name, now())
    }

    @Transactional
    fun getUserIdByTelegramId(telegramId: Int): Int? =
        userRepository.findByTelegramId(telegramId)?.id

    @Transactional
    fun getUserByTelegramId(telegramId: Int): User? = userRepository.findByTelegramId(telegramId)
}
