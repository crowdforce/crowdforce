package space.crowdforce.service.user

import dev.whyoleg.ktd.api.TdApi
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import space.crowdforce.repository.UserCodesRepository
import space.crowdforce.repository.UserRepository
import space.crowdforce.service.tg.TelegramService
import java.security.SecureRandom
import java.time.LocalDateTime
import kotlin.random.asKotlinRandom

@Service
class UserService(
    private val telegram: TelegramService,
    private val userRepository: UserRepository,
    private val userCodesRepository: UserCodesRepository,
    private val passwordEncoder: PasswordEncoder,
    private val transactionTemplate: TransactionTemplate
) {
    private val secureRandom = SecureRandom().asKotlinRandom()

    @Transactional
    suspend fun sendCodesToUser(userName: String) {
        val code = secureRandom.nextInt(100000, 999999).toString()
        val chat = telegram.searchPublicChat(userName)

        transactionTemplate.execute {
            val user = userRepository.findByUserName(userName) ?: userRepository.insert(userName)

            userCodesRepository.upsertUserCode(user.id, passwordEncoder.encode(code), LocalDateTime.now())
        }

        telegram.message(TdApi.SendMessage(
            chatId = chat.id,
            inputMessageContent = TdApi.InputMessageText(TdApi.FormattedText("Ваш код: $code"))
        ))
    }

    @Transactional
    fun getUserIdByName(userName: String): Int? =
        userRepository.findByUserName(userName)?.id
}
