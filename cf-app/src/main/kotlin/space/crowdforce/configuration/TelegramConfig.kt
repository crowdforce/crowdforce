package space.crowdforce.configuration

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import space.crowdforce.service.tg.CustomTelegramBot

@Configuration
class TelegramConfig {
    private val log = LoggerFactory.getLogger(TelegramConfig::class.java)

    @Autowired
    lateinit var crowdforce: CustomTelegramBot;

    @EventListener(ApplicationReadyEvent::class)
    fun botInit() {
        ApiContextInitializer.init();

        val botsApi = TelegramBotsApi()

        try {
            botsApi.registerBot(crowdforce)

            log.info("Crowdforce telegram bot started")
        } catch (e: TelegramApiException) {
            log.error("Bot registration was failed", e)
        }
    }
}
