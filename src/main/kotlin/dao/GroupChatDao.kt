package dao

import entries.GroupChatDBEntry

typealias GroupChatId = Long

interface GroupChatDao : ObjectDao<GroupChatDBEntry> {
    suspend fun addNewGroupChat(owner: UserId, chatName: String, uniqueLink: String?): GroupChatDBEntry?
    suspend fun searchByName(name: String): List<GroupChatDBEntry>
    suspend fun getChatByInviteLink(link: String): GroupChatDBEntry?
}