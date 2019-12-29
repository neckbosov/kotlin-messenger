package dao

import entries.PersonalChatDBEnrty

typealias PersonalChatId = Long

interface PersonalChatDao : ObjectDao<PersonalChatDBEnrty> {
    suspend fun addNewPersonalChat(member1: UserId, member2: UserId): PersonalChatDBEnrty?
    suspend fun selectWithUser(user: UserId): List<PersonalChatId>
}