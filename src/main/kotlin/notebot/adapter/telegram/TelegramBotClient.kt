package notebot.adapter.telegram

import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviour
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import notebot.adapter.notion.NotionClientAdapter
import notebot.domain.Subscription
import notebot.domain.enums.Status
import notebot.usecase.NoteUseCase
import notebot.usecase.RegisterUseCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TelegramBotClient(
    private val registerUseCase: RegisterUseCase,
    private val noteUseCase: NoteUseCase,
    private val token: String,
) {

    private val log: Logger = LoggerFactory.getLogger(NotionClientAdapter::class.java)

//    private val bot: Bot = telegramBot {
//        this.token = token
//        dispatch {
//            command("start") {
//                safeCall(message) {
//                    registerUseCase.start(message.chat.id)
//                    bot.sendMessage(
//                        ChatId.fromId(message.chat.id),
//                        "Привет. Введи токен ноушена"
//                    )
//                }
//            }
//            message(Custom {
//                registerUseCase.setPageId(chat.id, text ?: "")
//            }) {
//                safeCall(message) {
//                    bot.sendMessage(
//                        ChatId.fromId(message.chat.id),
//                        "Отлично."
//                    )
//                }
//            }
//            message(Custom {
//                registerUseCase.setNotionKey(chat.id, text ?: "")
//            }) {
//                safeCall(message) {
//                    bot.sendMessage(
//                        ChatId.fromId(message.chat.id),
//                        "Отлично. Теперь введи идентификатор страницы"
//                    )
//                }
//            }
//            message(Custom {
//                noteUseCase.note(chat.id, text ?: "")
//            }) {
//                safeCall(message) {
//                    bot.sendMessage(ChatId.fromId(message.chat.id), message.text ?: "")
//                }
//            }
//
//        }
//    }
//
//    private inline fun safeCall(message: Message, callback: Bot.() -> Unit) {
//        try {
//            bot.callback()
//        } catch (e: Exception) {
//            log.error("Error while handle message")
//            bot.sendMessage(ChatId.fromId(message.chat.id), "Произошла ошибка")
//        }
//    }

    suspend fun start() {
        val bot = telegramBot(token)

        bot.buildBehaviour {
            println(getMe())

            onCommand("start") {
                reply(it, "Hi")
                val notionToken = waitText(
                    SendTextMessage(
                        it.chat.id,
                        "Привет. Введи токен ноушена"
                    )
                ).first().text
                val pageId = waitText(
                    SendTextMessage(
                        it.chat.id,
                        "Отлично. Теперь введи идентификатор страницы"
                    )
                ).first().text
                val subs = Subscription(
                    chatId = it.chat.id.chatId,
                    notionAccessKey = notionToken,
                    notionPageId = pageId,
                    status = Status.COMPLETE
                )
                registerUseCase.register(subs)
            }.join()

            onText {
                noteUseCase.note(it.chat.id.chatId, it.content.text)
            }
        }
    }


}