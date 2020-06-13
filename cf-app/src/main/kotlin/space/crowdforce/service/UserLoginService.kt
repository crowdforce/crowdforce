package space.crowdforce.service

import dev.whyoleg.ktd.TelegramClient
import dev.whyoleg.ktd.api.TdApi
import dev.whyoleg.ktd.api.chat.searchPublicChat
import dev.whyoleg.ktd.api.message.message
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.exceptions.InvalidVerificationException
import space.crowdforce.repository.UserRepository

@Service
class UserLoginService(
    private val client: TelegramClient,
    private val userRepository: UserRepository,
    private val codesGeneratorService: CodesGeneratorService
) {

    @Transactional
    suspend fun sendCodesToUser(userName: String) =
        insertOrGet(userName)
            .let { user ->
                val code = codesGeneratorService.generateCode(user.id)
                val chat = client.searchPublicChat(user.tgUsername)
                client.message(TdApi.SendMessage(
                    chatId = chat.id,
                    inputMessageContent = TdApi.InputMessageText(TdApi.FormattedText("Ваш код: ${code.code}"))
                ))
            }

    @Transactional
    suspend fun verifyCode(userName: String, code: Int) =
        userRepository.findByUserName(userName)
            ?.let { codesGeneratorService.verifyCode(it.id, code) }
            ?.takeIf { it }
            ?: throw InvalidVerificationException("Unable to verify code")

    private fun insertOrGet(userName: String) =
        userRepository.findByUserName(userName)
            ?: userRepository.insert(userName)
}
