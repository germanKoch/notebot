package notebot.usecase

import notebot.adapter.notion.NotionClient
import notebot.adapter.repository.SubsPostgresClient
import notebot.domain.enums.Status

class NoteUseCase(
    private val notionClient: NotionClient,
    private val subsClient: SubsPostgresClient
) {

    fun note(chatId: Long, text: String): Boolean {
        return subsClient.getByChatId(chatId)?.let {
            if (it.status == Status.COMPLETE && text.isNotBlank()) {
                notionClient.addNote(it, text)
                true
            } else {
                false
            }
        } ?: false
    }

}