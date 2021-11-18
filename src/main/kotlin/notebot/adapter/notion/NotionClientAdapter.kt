package notebot.adapter.notion

import notebot.domain.Subscription
import notion.api.v1.NotionClient
import notion.api.v1.model.blocks.BulletedListItemBlock
import notion.api.v1.model.pages.PageProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NotionClientAdapter {

    private val log: Logger = LoggerFactory.getLogger(NotionClientAdapter::class.java)

    fun addNote(subscription: Subscription, text: String) {
        if (subscription.notionAccessKey != null && subscription.notionPageId != null) {
            val client = NotionClient(token = subscription.notionAccessKey)
            client.appendBlockChildren(
                subscription.notionPageId,
                listOf(
                    BulletedListItemBlock(
                        BulletedListItemBlock.Element(
                            listOf(
                                PageProperty.RichText(
                                    text = PageProperty.RichText.Text(text)
                                )
                            )
                        )
                    )
                )
            )
        }
    }
}