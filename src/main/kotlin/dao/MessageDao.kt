package dao

import entries.MessageDBEntry

typealias MessageId = Long

interface MessageDao : ObjectDao<MessageDBEntry> {
    suspend fun addNewMessage(from: UserId, isPersonal: Boolean, chat: Id, text: String): MessageDBEntry?
    suspend fun findByUser(user: UserId): List<MessageDBEntry>
    //    fun findSliceFromChat(type: Boolean, chat: Id, block: Int, last: Int? = null): List<MessageDBEntry>
    suspend fun getMessagesFromChat(isPersonal: Boolean, chat: Id): List<MessageDBEntry>
}