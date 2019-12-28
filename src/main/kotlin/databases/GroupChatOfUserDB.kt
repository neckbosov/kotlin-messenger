package databases

import dao.GroupChatId
import dao.GroupChatsOfUserDao
import dao.UserId
import entries.GroupChatDBEntry
import entries.GroupChatToUserDBEntry
import entries.UserDBEntry
import org.jetbrains.exposed.sql.and
import tables.DatabaseFactory.dbQuery
import tables.GroupChatsToUsers
import tables.getEntityID

class GroupChatOfUserDB : GroupChatsOfUserDao {
    override suspend fun add(key: UserId, value: GroupChatId): Boolean =
        dbQuery {
            val user = getEntityID<UserDBEntry>(key) ?: return@dbQuery false
            val chat = getEntityID<GroupChatDBEntry>(value) ?: return@dbQuery false
            GroupChatToUserDBEntry.new {
                this.chatId = chat
                this.userId = user
            }
            true
        }

    override suspend fun remove(key: UserId, value: GroupChatId) =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<GroupChatDBEntry>(value) ?: return@dbQuery false

            if (keyId.value == GroupChatDBEntry.findById(valueId.value)?.owner?.value) {
                return@dbQuery false
            }

            GroupChatToUserDBEntry
                .find { (GroupChatsToUsers.chatId eq valueId) and (GroupChatsToUsers.userId eq keyId) }
                .singleOrNull()?.delete() != null
        }

    override suspend fun select(key: UserId): List<GroupChatId> =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery emptyList<GroupChatId>()
            GroupChatToUserDBEntry.find { GroupChatsToUsers.userId eq keyId }.map { it.chatId.value }
        }

    override suspend fun contains(key: UserId, value: GroupChatId): Boolean =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<GroupChatDBEntry>(value) ?: return@dbQuery false
            !GroupChatToUserDBEntry
                .find { (GroupChatsToUsers.userId eq keyId) and (GroupChatsToUsers.chatId eq valueId) }
                .empty()
        }
}