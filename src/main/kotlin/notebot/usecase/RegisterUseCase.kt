package notebot.usecase

import notebot.adapter.repository.SubsPostgresClient
import notebot.domain.Subscription
import notebot.domain.enums.Status

class RegisterUseCase(
    private val subsClient: SubsPostgresClient
) {

    fun start(chatId: Long) {
        subsClient.save(
            Subscription(
                chatId = chatId,
                status = Status.NOTION_KEY_REQUESTED
            )
        )
    }

    fun setNotionKey(chatId: Long, notionKey: String): Boolean {
        return subsClient.getByChatId(chatId)?.let {
            if (it.status == Status.NOTION_KEY_REQUESTED && notionKey.isNotBlank()) {
                subsClient.update(it.copy(notionAccessKey = notionKey, status = Status.PAGE_ID_REQUESTED))
                true
            } else {
                false
            }
        } ?: false
    }

    fun setPageId(chatId: Long, pageId: String): Boolean {
        return subsClient.getByChatId(chatId)?.let {
            if (it.status == Status.PAGE_ID_REQUESTED && pageId.isNotBlank()) {
                subsClient.update(it.copy(notionPageId = pageId, status = Status.COMPLETE))
                true
            } else {
                false
            }
        } ?: false
    }
}