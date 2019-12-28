package databases

import dao.ContactsOfUserDao
import dao.Id
import dao.UserId
import entries.ContactDBEntry
import entries.UserDBEntry
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import tables.Contacts
import tables.DatabaseFactory.dbQuery
import tables.getEntityID

class ContactsOfUserDB : ContactsOfUserDao {
    override suspend fun add(key: UserId, value: Pair<UserId, String>): Boolean =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<UserDBEntry>(value.first) ?: return@dbQuery false
            Contacts.insertIgnoreAndGetId {
                it[userId] = keyId
                it[contactId] = valueId
                it[name] = value.second
            } != null
        }

    override suspend fun contains(key: UserId, value: Pair<UserId, String>): Boolean =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<UserDBEntry>(value.first) ?: return@dbQuery false
            !ContactDBEntry.find { (Contacts.userId eq keyId) and (Contacts.contactId eq valueId) }.empty()
        }

    override suspend fun remove(key: UserId, value: Pair<UserId, String>): Boolean =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery false
            val valueId = getEntityID<UserDBEntry>(value.first) ?: return@dbQuery false
            ContactDBEntry.find { (Contacts.userId eq keyId) and (Contacts.contactId eq valueId) }
                .singleOrNull()?.delete() != null
        }

    override suspend fun select(key: UserId): List<Pair<UserId, String>> =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(key) ?: return@dbQuery emptyList<Pair<UserId, String>>()
            ContactDBEntry.find { Contacts.userId eq keyId }.map { it.contactId.value to it.name }
        }

    override suspend fun changeName(userId: UserId, contactId: Id, name: String) =
        dbQuery {
            val keyId = getEntityID<UserDBEntry>(userId) ?: return@dbQuery
            val valueId = getEntityID<UserDBEntry>(contactId) ?: return@dbQuery
            ContactDBEntry.find {
                (Contacts.userId eq keyId) and (Contacts.contactId eq valueId)
            }.singleOrNull()?.name = name
        }
}