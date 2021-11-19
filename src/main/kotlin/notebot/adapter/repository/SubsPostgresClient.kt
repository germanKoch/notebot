package notebot.adapter.repository

import notebot.domain.Subscription
import notebot.domain.enums.Status
import java.sql.Connection
import java.sql.DriverManager

class SubsPostgresClient(private val url: String, private val login: String, private val pass: String) {

    fun execute(stats: Operation.() -> Unit) {
        DriverManager.getConnection(url, login, pass).use { conn ->
            val operation = Operation(conn)
            operation.stats()
        }
    }

    fun save(subscription: Subscription) {
        DriverManager.getConnection(url, login, pass).use { conn ->
            executeSaveStatement(subscription, conn)
        }
    }

    fun delete(chatId: Long) {
        DriverManager.getConnection(url, login, pass).use { conn ->
            executeDeleteStatement(chatId, conn)
        }
    }

    fun update(subscription: Subscription) {
        DriverManager.getConnection(url, login, pass).use { conn ->
            executeUpdateStatement(subscription, conn)
        }
    }

    fun getByChatId(chatId: Long): Subscription? {
        return DriverManager.getConnection(url, login, pass).use { conn ->
            conn.prepareStatement(sqlGet).use { stat ->
                stat.setLong(1, chatId)
                stat.execute()
                stat.resultSet.use {
                    if (it.next()) {
                        Subscription(
                            id = it.getLong("id"),
                            chatId = it.getLong("chat_id"),
                            notionAccessKey = it.getObject("notion_access_key") as? String,
                            notionPageId = it.getObject("notion_page_id") as? String,
                            status = Status.valueOf(it.getObject("status") as String)
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }

    class Operation(val conn: Connection) {
        fun save(subscription: Subscription) {
            executeSaveStatement(subscription, conn)
        }

        fun delete(chatId: Long) {
            executeDeleteStatement(chatId, conn)
        }

        fun update(subscription: Subscription) {
            executeUpdateStatement(subscription, conn)
        }
    }

    companion object {
        private fun executeSaveStatement(subscription: Subscription, conn: Connection) {
            conn.prepareStatement(sqlInsert).use {
                it.setLong(1, subscription.chatId)
                it.setString(2, subscription.notionAccessKey)
                it.setString(3, subscription.notionPageId)
                it.setString(4, subscription.status.name)
                it.executeUpdate()
            }
        }

        private fun executeDeleteStatement(chatId: Long, conn: Connection) {
            conn.prepareStatement(sqlDelete).use {
                it.setLong(1, chatId)
                it.executeUpdate()
            }
        }

        private fun executeUpdateStatement(subscription: Subscription, conn: Connection) {
            conn.prepareStatement(sqlUpdate).use { stat ->
                stat.setLong(1, subscription.chatId)
                stat.setString(2, subscription.notionAccessKey)
                stat.setString(3, subscription.notionPageId)
                stat.setString(4, subscription.status.name)
                stat.setLong(5, subscription.id!!)
                stat.executeUpdate()
            }
        }

        private const val tableName = "SUBSCRIPTION"
        private const val sqlDelete = "DELETE FROM $tableName WHERE chat_id = ?"
        private const val sqlInsert =
            "INSERT INTO $tableName (chat_id, notion_access_key, notion_page_id, status) VALUES (?, ?, ?, ?)"
        private const val sqlGet = "SELECT * FROM $tableName WHERE chat_id = ?"
        private const val sqlUpdate =
            "UPDATE $tableName SET chat_id = ?, notion_access_key = ?, notion_page_id = ?, status = ? WHERE id = ?"

    }
}