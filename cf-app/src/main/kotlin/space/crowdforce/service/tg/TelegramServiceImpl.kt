package space.crowdforce.service.tg

import dev.whyoleg.ktd.TelegramClient
import dev.whyoleg.ktd.api.TdApi
import dev.whyoleg.ktd.api.chat.searchPublicChat
import dev.whyoleg.ktd.api.message.message
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("prod")
class TelegramServiceImpl(
    private val telegramClient: TelegramClient
) : TelegramService {
    override suspend fun searchPublicChat(
        username: String?
    ): TdApi.Chat = telegramClient.searchPublicChat(username)

    /**
     * Sends a message
     * Returns the sent message
     */
    override suspend fun message(
        f: TdApi.SendMessage
    ): TdApi.Message = telegramClient.message(f)
}
