package notebot.adapter.notion

import org.jraf.klibnotion.client.Authentication
import org.jraf.klibnotion.client.ClientConfiguration
import org.jraf.klibnotion.client.NotionClient

//"secret_pNmklWWxdt0PyJAz1ArpbU5KqhzBmI93W4Lkcc0PKY7"
//"6103591ef5924b448a177b0a9497d165"

class NotionClient(accessToken: String, private val pageId: String) {

    private val notionClient: NotionClient = NotionClient.newInstance(
        ClientConfiguration(Authentication(accessToken))
    )

    fun addNote(text: String) {
        suspend {
            notionClient.blocks.appendBlockList(pageId) { bullet(text) }
        }
    }

}