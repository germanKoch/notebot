package notebot.usecase

import notebot.adapter.repository.SubsPostgresClient
import notebot.domain.Subscription

class RegisterUseCase(
    private val subsClient: SubsPostgresClient
) {

    fun register(subs: Subscription) {
        subsClient.execute {
            delete(subs.chatId)
            save(subs)
        }
    }
}