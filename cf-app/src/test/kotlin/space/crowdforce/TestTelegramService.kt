package space.crowdforce

import dev.whyoleg.ktd.api.TdApi
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import space.crowdforce.service.tg.TelegramService

@Component
@Profile("test")
class TestTelegramService : TelegramService {
    override suspend fun searchPublicChat(username: String?): TdApi.Chat {
        return TdApi.Chat(1, TdApi.ChatTypeBasicGroup(1), null, "test", null, TdApi.ChatPermissions(),
            null, 1, false, false, false, false, false, false,
            false, false, 1, 1, 1, 1,
            TdApi.ChatNotificationSettings(false, 1,false,"tt", false, false,
                false, false, false, false), null, 1, 1, null, "test")
    }

    override suspend fun message(f: TdApi.SendMessage): TdApi.Message {
        return TdApi.Message(1, 1, 1, null, null, false,
            false, false, false, false,
            false,
            false, 123, 123, null, 12, 12,
            12.21, 12, "test", 1, 1, "test",
            TdApi.MessageContactRegistered(), null)
    }
}
