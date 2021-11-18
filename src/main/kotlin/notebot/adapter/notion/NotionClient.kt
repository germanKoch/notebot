package notebot.adapter.notion

import notebot.domain.Subscription
import org.jraf.klibnotion.client.Authentication
import org.jraf.klibnotion.client.ClientConfiguration
import org.jraf.klibnotion.client.NotionClient

class NotionClient {

    fun addNote(subscription: Subscription, text: String) {
            if (subscription.notionAccessKey != null && subscription.notionPageId != null) {
                val notionClient =
                    NotionClient.newInstance(ClientConfiguration(Authentication(subscription.notionAccessKey)))
        suspend {
                notionClient.blocks.appendBlockList(subscription.notionPageId) { bullet(text) }
                print("aaa")
            }
        }
    }

}