package databases

import dao.GroupChatId
import dao.MembersOfGroupChatDao
import dao.UserId
import entries.GroupChatDBEntry
import entries.GroupChatToUserDBEntry
import entries.UserDBEntry
import org.jetbrains.exposed.sql.and
import tables.DatabaseFactory.dbQuery
import tables.GroupChatsToUsers
import tables.getEntityID

class MembersOfGroupChatDB : MembersOfGroupChatDao {
    override suspend fun add(key: GroupChatId, value: UserId): Boolean =
        dbQuery {
            val user = getEntityID<UserDBEntry>(value) ?: return@dbQuery false
            val chat = getEntityID<GroupChatDBEntry>(key) ?: return@dbQuery false
            GroupChatToUserDBEntry.new {
                this.chatId = chat
                this.userId = user
            }
            true
        }

    override suspend fun remove(key: GroupChatId, value: UserId) =
        dbQuery {
            val keyId = getEntityID<GroupChatDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<UserDBEntry>(value) ?: return@dbQuery false

            if (valueId.value == GroupChatDBEntry.findById(keyId.value)?.owner?.value) {
                return@dbQuery false
            }

            GroupChatToUserDBEntry
                .find {
                    (GroupChatsToUsers.chatId eq keyId) and (GroupChatsToUsers.userId eq valueId)
                }.singleOrNull()?.delete() != null
        }

    override suspend fun select(key: GroupChatId) =
        dbQuery {
            val keyId = getEntityID<GroupChatDBEntry>(key) ?: return@dbQuery emptyList<UserId>()
            GroupChatToUserDBEntry.find { GroupChatsToUsers.chatId eq keyId }.map { it.userId.value }
        }

    override suspend fun contains(key: GroupChatId, value: UserId): Boolean =
        dbQuery {
            val keyId = getEntityID<GroupChatDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<UserDBEntry>(value) ?: return@dbQuery false
            !GroupChatToUserDBEntry
                .find { (GroupChatsToUsers.chatId eq keyId) and (GroupChatsToUsers.userId eq valueId) }
                .empty()
        }
}