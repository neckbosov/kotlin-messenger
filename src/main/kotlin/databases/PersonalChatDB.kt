package databases

import dao.Id
import dao.PersonalChatDao
import dao.PersonalChatId
import dao.UserId
import entries.PersonalChatDBEnrty
import entries.UserDBEntry
import org.jetbrains.exposed.sql.or
import tables.DatabaseFactory.dbQuery
import tables.PersonalChats

class PersonalChatDB : PersonalChatDao {
    override suspend fun addNewPersonalChat(member1: UserId, member2: UserId) =
        dbQuery {
            val id1 = UserDBEntry.findById(member1)?.id ?: return@dbQuery null
            val id2 = UserDBEntry.findById(member2)?.id ?: return@dbQuery null
            PersonalChatDBEnrty.new {
                this.member1 = id1
                this.member2 = id2
            }
        }

    override suspend fun getById(elemId: Id) =
        dbQuery { PersonalChatDBEnrty.findById(elemId) }

    override suspend fun deleteById(elemId: Id) =
        dbQuery { PersonalChatDBEnrty.findById(elemId)?.delete() ?: Unit }

    override suspend fun size(): Int = dbQuery { PersonalChatDBEnrty.all().count() }

    override suspend fun selectWithUser(user: UserId): List<PersonalChatId> =
        dbQuery {
            PersonalChatDBEnrty
                .find { (PersonalChats.member1 eq user) or (PersonalChats.member2 eq user) }
                .map { it.id.value }
        }
}