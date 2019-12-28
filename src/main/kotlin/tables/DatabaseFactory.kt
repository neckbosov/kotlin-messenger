package tables

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            "127.0.0.1:5432", driver = "org.postgresql.Driver",
            user = "messenger", password = "123456"
        )

        transaction {
            SchemaUtils.drop(BlockedUsers)
            SchemaUtils.drop(Contacts)
            SchemaUtils.drop(GroupChatsToUsers)
            SchemaUtils.drop(GroupChats)
            SchemaUtils.drop(Messages)
            SchemaUtils.drop(PersonalChats)
            SchemaUtils.drop(Users)
        }

        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(GroupChats)
            SchemaUtils.create(PersonalChats)
            SchemaUtils.create(Messages)
            SchemaUtils.create(GroupChatsToUsers)
            SchemaUtils.create(Contacts)
            SchemaUtils.create(BlockedUsers)
        }
    }

    suspend fun <T> dbQuery(
        block: suspend () -> T
    ): T = newSuspendedTransaction { block() }
}