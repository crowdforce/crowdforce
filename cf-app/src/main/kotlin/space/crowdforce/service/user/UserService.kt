package space.crowdforce.service.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.User
import space.crowdforce.domain.UserIdentityKey
import space.crowdforce.repository.UserRepository
import java.time.LocalDateTime.now

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun getOrCreateUser(userIdentityKey: UserIdentityKey, name: String): User {
        return userRepository.findByIdentityKey(userIdentityKey) ?: userRepository.insert(userIdentityKey, name, now())
    }

    @Transactional
    fun getByUserId(userId: Int): User? =
        userRepository.findByUserId(userId)
}
