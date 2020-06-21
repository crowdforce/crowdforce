package space.crowdforce.service.tg

import dev.whyoleg.ktd.api.TdApi

interface TelegramService {
    suspend fun searchPublicChat(username: String? = null): TdApi.Chat

    /**
     * Sends a message
     * Returns the sent message
     */
    suspend fun message(f: TdApi.SendMessage): TdApi.Message
}
