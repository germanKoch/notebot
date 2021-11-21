package notebot

import notebot.adapter.notion.NotionClientAdapter
import notebot.adapter.repository.SubsPostgresClient
import notebot.adapter.telegram.TelegramBotClient
import notebot.usecase.NoteUseCase
import notebot.usecase.RegisterUseCase

suspend fun main() {
    val subsClient = SubsPostgresClient("jdbc:postgresql://localhost:5432/notebot", "postgres", "postgres")
    val notionClient = NotionClientAdapter()
    val noteCase = NoteUseCase(notionClient, subsClient)
    val registerCase = RegisterUseCase(subsClient)
    val telegramBotClient = TelegramBotClient(registerCase, noteCase, System.getenv("TELEGRAM_TOKEN"))

    telegramBotClient.start()
}