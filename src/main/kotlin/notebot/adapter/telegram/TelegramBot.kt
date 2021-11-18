package notebot.adapter.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.bot as telegramBot

//"2142829347:AAEtMADR5XEex2FLMqCOrX2zUWLaSypi8K8"

class TelegramBot(token: String) {

    private val bot: Bot = telegramBot {
        this.token = token

        dispatch {
            text {
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = text)
            }
        }
    }

    fun start() {
        bot.startPolling()
    }

}