package databases

import dao.BlockedUsersDao
import dao.UserId
import entries.BlockingOfUserDBEntry
import entries.UserDBEntry
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import tables.BlockedUsers
import tables.DatabaseFactory.dbQuery
import tables.getEntityID

class BlockedUsersDB : BlockedUsersDao {
    override suspend fun add(key: UserId, value: UserId): Boolean =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<UserDBEntry>(value) ?: return@dbQuery false
            BlockingOfUserDBEntry.new {
                user = keyId
                blockedUser = valueId
            }
            true
        }

    override suspend fun remove(key: UserId, value: UserId) =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<UserDBEntry>(value) ?: return@dbQuery false
            val expr = (BlockedUsers.user eq keyId) and (BlockedUsers.blockedUser eq valueId)
            BlockingOfUserDBEntry.find { expr }.singleOrNull()?.delete() != null
        }

    override suspend fun select(key: UserId) =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery emptyList<UserId>()
            BlockedUsers.select { BlockedUsers.user eq keyId }.map { it[BlockedUsers.blockedUser].value }
        }

    override suspend fun contains(key: UserId, value: UserId): Boolean =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<UserDBEntry>(value) ?: return@dbQuery false
            !BlockedUsers.select {
                (BlockedUsers.user eq keyId) and (BlockedUsers.blockedUser eq valueId)
            }.empty()
        }
}