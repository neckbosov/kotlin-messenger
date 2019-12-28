package dao

interface BlockedUsersDao : RelationsDao<UserId, UserId> {
    suspend fun isBlocked(user: UserId, potentiallyBlockedUser: UserId): Boolean =
        contains(user, potentiallyBlockedUser)

    suspend fun block(user: UserId, blockedUser: UserId) = add(user, blockedUser)
    suspend fun unblock(user: UserId, blockedUser: UserId) = remove(user, blockedUser)
}