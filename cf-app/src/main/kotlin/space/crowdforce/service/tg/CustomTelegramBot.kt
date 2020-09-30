package space.crowdforce.service.tg

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Component
class CustomTelegramBot(
        private val tgCommandProcessor: CommandProcessor,
        private @Value("\${telegram.bot.token}") val customBotToken: String,
        private @Value("\${telegram.bot.username}") val customBotUsername: String
) : TelegramLongPollingBot(DefaultBotOptions()) {
    companion object {
        private val log = LoggerFactory.getLogger(CustomTelegramBot::class.java)
    }

    override fun getBotUsername(): String {
        return customBotUsername
    }

    override fun getBotToken(): String {
        return customBotToken
    }

    fun sendMsg(targetId: String, msg: String, actions: List<Pair<String, String>> = emptyList()) {

        val message = SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(targetId)
                .setReplyMarkup(inlineKeyboardMarkup(actions))
                .setText(msg)

        try {
            execute(message) // Call method to send the message
        } catch (e: TelegramApiException) {
            log.error("Replacing message failed : chatId=$targetId, message=$msg, actions=$actions", e)
        }
    }

    fun replaceMsg(targetId: String, messageId: Int, msg: String, actions: List<Pair<String, String>> = emptyList()) {
        val message = EditMessageText().setChatId(targetId)
                .setMessageId(messageId)
                .setText(msg)
                .setReplyMarkup(inlineKeyboardMarkup(actions))
        try {
            execute(message) // Call method to send the message
        } catch (e: TelegramApiException) {
            log.error(
                "Replacing message failed : chatId=$targetId, messageId=$messageId, message=$msg, actions=$actions", e
            )
        }
    }

    private fun inlineKeyboardMarkup(actions: List<Pair<String, String>>): InlineKeyboardMarkup {
        val inlineKeyboardMarkup = InlineKeyboardMarkup()

        val chunkedSize = 1

        inlineKeyboardMarkup.keyboard = actions.map {
            val button = InlineKeyboardButton(it.first)
            button.setCallbackData(it.second)
            button
        }.chunked(chunkedSize)

        return inlineKeyboardMarkup
    }


    override fun onUpdateReceived(update: Update) {
        // We check if the update has a message and the message has text
        if (update.hasEditedMessage() && update.editedMessage.hasText()) {
            tgCommandProcessor.executeEditedText(
                    update.editedMessage.chatId.toString(),
                    update.editedMessage.messageId.toString(),
                    update.editedMessage.text
            )
        } else if (update.hasMessage() && update.message.hasText()) {
            val answer = tgCommandProcessor.executeText(
                    update.message.chatId.toString(),
                    update.message.messageId.toString(),
                    update.message.text
            )

            sendMsg(update.message.chatId.toString(), answer.text, answer.actions)
        } else if (update.hasCallbackQuery()) {
            val message = update.callbackQuery.message
            val answer = tgCommandProcessor.execute(
                    message.chatId.toString(),
                    update.callbackQuery.data
            )

            sendMsg(message.chatId.toString(), answer.text, answer.actions)
//                replaceMsg(message.chatId.toString(), message.messageId, answer.text, answer.actions)
        }
    }
}
