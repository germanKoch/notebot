package notebot

import notebot.adapter.notion.NotionClientAdapter
import notebot.adapter.repository.SubsPostgresClient
import notebot.adapter.telegram.TelegramBotClient
import notebot.usecase.NoteUseCase
import notebot.usecase.RegisterUseCase

//NOTION
//"secret_pNmklWWxdt0PyJAz1ArpbU5KqhzBmI93W4Lkcc0PKY7"
//"6103591ef5924b448a177b0a9497d165"

//TELEGRAM
//"2142829347:AAEtMADR5XEex2FLMqCOrX2zUWLaSypi8K8"

fun main() {
    val subsClient = SubsPostgresClient("jdbc:postgresql://localhost:5432/notebot", "postgres", "postgres")
    val notionClient = NotionClientAdapter()
    val noteCase = NoteUseCase(notionClient, subsClient)
    val registerCase = RegisterUseCase(subsClient)
    val telegramBotClient = TelegramBotClient(registerCase, noteCase, "2142829347:AAEtMADR5XEex2FLMqCOrX2zUWLaSypi8K8")

    telegramBotClient.start()
}