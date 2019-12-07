package databases

import dao.Id
import dao.PersonalChatDao
import dao.PersonalChatId
import dao.UserId
import model.PersonalChat
import model.User
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import tables.PersonalChats

class PersonalChatDB : PersonalChatDao {
    override fun addNewPersonalChat(member1: UserId, member2: UserId) =
        transaction {
            val id1 = User.findById(member1)?.id ?: return@transaction null
            val id2 = User.findById(member2)?.id ?: return@transaction null
            PersonalChat.new {
                this.member1 = id1
                this.member2 = id2
            }
        }

    override fun getById(elemId: Id) =
        transaction { PersonalChat.findById(elemId) }

    override fun deleteById(elemId: Id) =
        transaction { PersonalChat.findById(elemId)?.delete() ?: Unit }

    override val size: Int
        get() = transaction { PersonalChat.all().count() }

    override fun selectWithUser(user: UserId): List<PersonalChatId> =
        transaction {
            PersonalChat
                .find { (PersonalChats.member1 eq user) or (PersonalChats.member2 eq user) }
                .map { it.id.value }
        }
}