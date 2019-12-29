package databases

import dao.Id
import dao.MessageDao
import dao.UserId
import entries.GroupChatDBEntry
import entries.MessageDBEntry
import entries.PersonalChatDBEnrty
import entries.UserDBEntry
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.joda.time.DateTime
import tables.DatabaseFactory.dbQuery
import tables.Messages

class MessageDB : MessageDao {
    override suspend fun addNewMessage(from: UserId, isPersonal: Boolean, chat: Id, text: String) =
        dbQuery {
            val fromId = UserDBEntry.findById(from)?.id ?: return@dbQuery null
            val chatId: Id

            if (isPersonal)
                chatId = PersonalChatDBEnrty.findById(chat)?.id?.value ?: return@dbQuery null
            else
                chatId = GroupChatDBEntry.findById(chat)?.id?.value ?: return@dbQuery null

            MessageDBEntry.new {
                this.from = fromId
                this.isPersonal = isPersonal
                this.chat = chatId
                this.text = text
                this.dateTime = DateTime.now()
                this.isDeleted = false
                this.isEdited = false
            }
        }

    override suspend fun getById(elemId: Id) = dbQuery { MessageDBEntry.findById(elemId) }

    override suspend fun deleteById(elemId: Id) =
        dbQuery { MessageDBEntry.findById(elemId)?.delete() ?: Unit }

    override suspend fun findByUser(user: UserId) =
        dbQuery { MessageDBEntry.find { Messages.from eq user }.toList() }

//    override suspend fun findSliceFromChat(isPersonal: Boolean, chat: Id, block: Int, last: Int?) =
//        dbQuery {
//            val chatMessages =
//                MessageDBEntry
//                    .find { (Messages.isPersonal eq isPersonal) and (Messages.chat eq chat) }
//                    .orderBy(Messages.dateTime to SortOrder.ASC)
//            val offset = last ?: (maxOf(chatMessages.count() - block, 0))
//            chatMessages.limit(block, offset).toList().reversed()
//        }

    override suspend fun getMessagesFromChat(isPersonal: Boolean, chat: Id): List<MessageDBEntry> =
        dbQuery {
            MessageDBEntry
                .find { (Messages.isPersonal eq isPersonal) and (Messages.chat eq chat) }
                .orderBy(Messages.dateTime to SortOrder.ASC).toList()
        }

    override suspend fun size() = dbQuery { MessageDBEntry.all().count() }
}