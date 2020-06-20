package space.crowdforce.service.user

import dev.whyoleg.ktd.TelegramClient
import dev.whyoleg.ktd.api.TdApi
import dev.whyoleg.ktd.api.chat.searchPublicChat
import dev.whyoleg.ktd.api.message.message
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import space.crowdforce.repository.UserCodesRepository
import space.crowdforce.repository.UserRepository
import java.security.SecureRandom
import java.time.LocalDateTime
import kotlin.random.asKotlinRandom

@Service
class UserService(
        private val client: TelegramClient,
        private val userRepository: UserRepository,
        private val userCodesRepository: UserCodesRepository,
        private val passwordEncoder: PasswordEncoder,
        private val transactionTemplate: TransactionTemplate
) {
    private val secureRandom = SecureRandom().asKotlinRandom()

    @Transactional
    suspend fun sendCodesToUser(userName: String) {
        val code = secureRandom.nextInt(100000, 999999).toString()
        val chat = client.searchPublicChat(userName)

        transactionTemplate.execute {
            val user = userRepository.findByUserName(userName) ?: userRepository.insert(userName)

            userCodesRepository.upsertUserCode(user.id, passwordEncoder.encode(code), LocalDateTime.now())
        }

        client.message(TdApi.SendMessage(
                chatId = chat.id,
                inputMessageContent = TdApi.InputMessageText(TdApi.FormattedText("Ваш код: $code"))
        ))
    }
}

