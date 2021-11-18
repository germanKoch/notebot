package notebot.domain

import notebot.domain.enums.Status

data class Subscription(
    val id: Long? = null,
    val chatId: Long,
    val notionAccessKey: String? = null,
    val notionPageId: String? = null,
    val status: Status
)
