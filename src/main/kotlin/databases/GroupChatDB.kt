package databases

import dao.GroupChatDao
import dao.Id
import dao.UserId
import entries.GroupChatDBEntry
import entries.GroupChatToUserDBEntry
import entries.UserDBEntry
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import tables.DatabaseFactory.dbQuery
import tables.GroupChats
import tables.GroupChatsToUsers
import tables.getEntityID


class GroupChatDB : GroupChatDao {
    override suspend fun addNewGroupChat(owner: UserId, chatName: String, uniqueLink: String?) =
        dbQuery {
            val ownerID = getEntityID<UserDBEntry>(owner) ?: return@dbQuery null
            GroupChatDBEntry.new {
                this.owner = ownerID
                this.chatName = chatName
                this.uniqueLink = uniqueLink
            }.also {
                GroupChatToUserDBEntry.new {
                    this.chatId = it.id
                    this.userId = ownerID
                }
            }
        }

    override suspend fun getById(elemId: Id) =
        dbQuery { GroupChatDBEntry.findById(elemId) }

    override suspend fun deleteById(elemId: Id) =
        dbQuery {
            val chat = GroupChatDBEntry.findById(elemId) ?: return@dbQuery
            if (!GroupChatsToUsers.select { GroupChatsToUsers.chatId eq chat.id }.empty()) {
                GroupChatsToUsers.deleteWhere { GroupChatsToUsers.chatId eq chat.id }
            }
            chat.delete()
        }

    override suspend fun size(): Int = dbQuery { GroupChatDBEntry.all().count() }

    override suspend fun searchByName(name: String) =
        dbQuery { GroupChatDBEntry.find { GroupChats.chatName eq name }.toList() }

    override suspend fun getChatByInviteLink(link: String) =
        dbQuery { GroupChatDBEntry.find { GroupChats.uniqueLink eq link }.singleOrNull() }
}