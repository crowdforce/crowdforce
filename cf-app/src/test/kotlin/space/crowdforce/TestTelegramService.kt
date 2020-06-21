package space.crowdforce

import dev.whyoleg.ktd.api.TdApi
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import space.crowdforce.service.tg.TelegramService

@Component
@Profile("test")
class TestTelegramService : TelegramService {
    override suspend fun searchPublicChat(username: String?): TdApi.Chat {
        TODO("not implemented")
    }

    override suspend fun message(f: TdApi.SendMessage): TdApi.Message {
        TODO("not implemented")
    }
}
