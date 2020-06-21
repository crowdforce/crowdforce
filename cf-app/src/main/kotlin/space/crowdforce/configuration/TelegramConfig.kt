package space.crowdforce.configuration

import dev.whyoleg.ktd.Telegram
import dev.whyoleg.ktd.TelegramClientConfiguration
import dev.whyoleg.ktd.api.TdApi
import dev.whyoleg.ktd.api.check.checkDatabaseEncryptionKey
import dev.whyoleg.ktd.api.tdlib.setTdlibParameters
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Configuration
class TelegramConfiguration {
    @Bean
    fun telegram() = Telegram(
        configuration = TelegramClientConfiguration(
            maxEventsCount = 100
        )
    )

    @Bean
    fun client(telegram: Telegram, telegramProperties: TelegramProperties) =
        telegram.client()
            .also {
                runBlocking {

                    Files.write(
                        Paths.get(telegramProperties.databaseDirectory + "/" + "td.binlog"),
                        Base64.getDecoder().decode(telegramProperties.binLogBase64)
                    )

                    it.setTdlibParameters(TdApi.TdlibParameters(
                        apiId = telegramProperties.apiId,
                        apiHash = telegramProperties.apiHash,
                        applicationVersion = telegramProperties.applicationVersion,
                        databaseDirectory = telegramProperties.databaseDirectory,
                        deviceModel = telegramProperties.deviceModel,
                        filesDirectory = telegramProperties.filesDirectory,
                        systemLanguageCode = telegramProperties.systemLanguageCode,
                        systemVersion = telegramProperties.systemVersion
                    ))

                    it.checkDatabaseEncryptionKey(telegramProperties.databaseEncryptionKey.toByteArray())
                }
            }
}

@Validated
@Component
@ConfigurationProperties(prefix = "telegram")
class TelegramProperties {
    @Min(1)
    var apiId: Int = 1

    @NotBlank
    lateinit var apiHash: String

    @NotBlank
    lateinit var applicationVersion: String

    @NotBlank
    lateinit var databaseDirectory: String

    @NotBlank
    lateinit var deviceModel: String

    @NotBlank
    lateinit var filesDirectory: String

    @NotBlank
    lateinit var systemLanguageCode: String

    @NotBlank
    lateinit var systemVersion: String

    @NotBlank
    lateinit var databaseEncryptionKey: String

    @NotBlank
    lateinit var binLogBase64: String
}
