package notebot.adapter.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.extensions.filters.Filter.Custom
import notebot.usecase.NoteUseCase
import notebot.usecase.RegisterUseCase
import com.github.kotlintelegrambot.bot as telegramBot

class TelegramBotClient(
    private val registerUseCase: RegisterUseCase,
    private val noteUseCase: NoteUseCase,
    token: String,
) {

    private val bot: Bot = telegramBot {
        this.token = token
        dispatch {
            message(Custom {
                noteUseCase.note(chat.id, text ?: "")
            }) {
                bot.sendMessage(ChatId.fromId(message.chat.id), message.text ?: "")
            }
            message(Custom {
                registerUseCase.setPageId(chat.id, text ?: "")
            }) {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    "Отлично."
                )
            }
            message(Custom {
                registerUseCase.setNotionKey(chat.id, text ?: "")
            }) {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    "Отлично. Теперь введи идентификатор страницы"
                )
            }
            command("start") {
                registerUseCase.start(message.chat.id)
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    "Привет. Введи токен ноушена"
                )
            }
        }
    }

    fun start() {
        bot.startPolling()
    }

}