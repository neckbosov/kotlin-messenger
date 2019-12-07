package databases

import dao.ContactsOfUserDao
import dao.Id
import dao.UserId
import model.Contact
import model.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import tables.Contacts
import tables.getEntityID

class ContactsOfUserDB : ContactsOfUserDao {
    override fun add(key: UserId, value: Pair<UserId, String>): Boolean =
        transaction {
            val keyId = getEntityID<User>(key) ?: return@transaction false
            val valueId = getEntityID<User>(value.first) ?: return@transaction false
            Contacts.insertIgnoreAndGetId {
                it[userId] = keyId
                it[contactId] = valueId
                it[name] = value.second
            } != null
        }

    override fun contains(key: UserId, value: Pair<UserId, String>): Boolean =
        transaction {
            val keyId = getEntityID<User>(key) ?: return@transaction false
            val valueId = getEntityID<User>(value.first) ?: return@transaction false
            !Contact.find { (Contacts.userId eq keyId) and (Contacts.contactId eq valueId) }.empty()
        }

    override fun remove(key: UserId, value: Pair<UserId, String>): Boolean =
        transaction {
            val keyId = getEntityID<User>(key) ?: return@transaction false
            val valueId = getEntityID<User>(value.first) ?: return@transaction false
            Contact.find { (Contacts.userId eq keyId) and (Contacts.contactId eq valueId) }
                .singleOrNull()?.delete() != null
        }

    override fun select(key: UserId): List<Pair<UserId, String>> =
        transaction {
            val keyId = getEntityID<User>(key) ?: return@transaction emptyList<Pair<UserId, String>>()
            Contact.find { Contacts.userId eq keyId }.map { it.contactId.value to it.name }
        }

    override fun changeName(userId: UserId, contactId: Id, name: String) =
        transaction {
            val keyId = getEntityID<User>(userId) ?: return@transaction
            val valueId = getEntityID<User>(contactId) ?: return@transaction
            Contact.find {
                (Contacts.userId eq keyId) and (Contacts.contactId eq valueId)
            }.singleOrNull()?.name = name
        }
}