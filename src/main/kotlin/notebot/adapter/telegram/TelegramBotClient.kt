package notebot.adapter.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter.Custom
import notebot.adapter.notion.NotionClientAdapter
import notebot.usecase.NoteUseCase
import notebot.usecase.RegisterUseCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.github.kotlintelegrambot.bot as telegramBot

class TelegramBotClient(
    private val registerUseCase: RegisterUseCase,
    private val noteUseCase: NoteUseCase,
    token: String,
) {

    private val log: Logger = LoggerFactory.getLogger(NotionClientAdapter::class.java)

    private val bot: Bot = telegramBot {
        this.token = token
        dispatch {
            command("start") {
                safeCall(message) {
                    registerUseCase.start(message.chat.id)
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        "Привет. Введи токен ноушена"
                    )
                }
            }
            message(Custom {
                registerUseCase.setPageId(chat.id, text ?: "")
            }) {
                safeCall(message) {
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        "Отлично."
                    )
                }
            }
            message(Custom {
                registerUseCase.setNotionKey(chat.id, text ?: "")
            }) {
                safeCall(message) {
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        "Отлично. Теперь введи идентификатор страницы"
                    )
                }
            }
            message(Custom {
                noteUseCase.note(chat.id, text ?: "")
            }) {
                safeCall(message) {
                    bot.sendMessage(ChatId.fromId(message.chat.id), message.text ?: "")
                }
            }

        }
    }

    private inline fun safeCall(message: Message, callback: Bot.() -> Unit) {
        try {
            bot.callback()
        } catch (e: Exception) {
            log.error("Error while handle message")
            bot.sendMessage(ChatId.fromId(message.chat.id), "Произошла ошибка")
        }
    }

    fun start() {
        bot.startPolling()
    }


}