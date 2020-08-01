package space.crowdforce

import dev.whyoleg.ktd.Telegram
import dev.whyoleg.ktd.TelegramClientConfiguration
import dev.whyoleg.ktd.api.TdApi
import dev.whyoleg.ktd.api.authentication.setAuthenticationPhoneNumber
import dev.whyoleg.ktd.api.check.checkAuthenticationCode
import dev.whyoleg.ktd.api.check.checkDatabaseEncryptionKey
import dev.whyoleg.ktd.api.tdlib.setTdlibParameters
import kotlinx.coroutines.runBlocking

fun main() {
    action(1, "61177")
}

fun action(code: Int, param: String) {
    val TELEGRAM_API_ID = 123
    val TELEGRAM_API_HASH = "<code>"
    val TELEGRAM_DB_ENCRYPTION_KEY = "<code>"
    val TELEGRAM_DB_DIR = "telegram"
    val DEVICE_MODEL = "model"
    val SYSTEM_VERSION = "1"
    val SYSTEM_LANGUAGE_CODE = "eu"
    val APP_VERSION = "1.0.0"

    Telegram(
        configuration = TelegramClientConfiguration(
            maxEventsCount = 100
        )
    ).client()
        .also {
            runBlocking {
                it.setTdlibParameters(TdApi.TdlibParameters(
                    apiId = TELEGRAM_API_ID,
                    apiHash = TELEGRAM_API_HASH,
                    applicationVersion = APP_VERSION,
                    databaseDirectory = TELEGRAM_DB_DIR,
                    deviceModel = DEVICE_MODEL,
                    filesDirectory = TELEGRAM_DB_DIR,
                    systemLanguageCode = SYSTEM_LANGUAGE_CODE,
                    systemVersion = SYSTEM_VERSION
                ))

                it.checkDatabaseEncryptionKey(TELEGRAM_DB_ENCRYPTION_KEY.toByteArray())

                if (code == 0)
                    it.setAuthenticationPhoneNumber(phoneNumber = param)

                if (code == 1)
                    it.checkAuthenticationCode(code = param)
            }
        }
}