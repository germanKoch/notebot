package notebot.adapter.telegram

import dev.inmo.tgbotapi.bot.Ktor.telegramBot
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
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
    token: String,
) {

    private val log: Logger = LoggerFactory.getLogger(NotionClientAdapter::class.java)
    private val bot: TelegramBot = telegramBot(token)

    suspend fun start() {
        bot.buildBehaviourWithLongPolling {
            println(getMe())

            onCommand("start") {
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
                safeCall(it) {
                    registerUseCase.register(subs)
                    sendMessage(it.chat.id, "Отлично")
                }
            }

            onText {
                safeCall(it) {
                    noteUseCase.note(it.chat.id.chatId, it.content.text)
                    sendMessage(it.chat.id, it.content.text)
                }
            }
        }.join()
    }

    private suspend inline fun safeCall(message: Message, callback: () -> Unit) {
        try {
            callback()
        } catch (e: Exception) {
            log.error("Error while handle message", e)
            bot.sendMessage(message.chat.id, "Произошла ошибка")
        }
    }


}