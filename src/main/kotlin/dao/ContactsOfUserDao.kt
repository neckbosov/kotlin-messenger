package dao

interface ContactsOfUserDao : RelationsDao<UserId, Pair<UserId, String>> {
    suspend fun changeName(userId: UserId, contactId: UserId, name: String)
}