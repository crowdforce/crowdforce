package space.crowdforce.service

import dev.whyoleg.ktd.TelegramClient
import dev.whyoleg.ktd.api.TdApi
import dev.whyoleg.ktd.api.chat.searchPublicChat
import dev.whyoleg.ktd.api.message.message
import org.springframework.stereotype.Service
import space.crowdforce.repository.UserRepository

@Service
class UserLoginService(
    private val client: TelegramClient,
    private val userRepository: UserRepository,
    private val codesGeneratorService: CodesGeneratorService
) {

    suspend fun sendCodesToUser(userName: String) {
        insertOrGet(userName)
            .let { user ->
                val code = codesGeneratorService.generateCode(user.id)
                val chat = client.searchPublicChat(user.tgUsername)
                client.message(TdApi.SendMessage(
                    chatId = chat.id,
                    inputMessageContent = TdApi.InputMessageText(TdApi.FormattedText("Ваш код: ${code.code}"))
                ))
            }
    }

    private fun insertOrGet(userName: String) =
        userRepository.findByUserName(userName)
            ?: userRepository.insert(userName)
}
