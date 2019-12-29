package databases

import dao.UserDao
import dao.UserId
import entries.UserDBEntry
import io.ktor.auth.UserPasswordCredential
import org.jetbrains.exposed.sql.and
import tables.DatabaseFactory.dbQuery
import tables.Users

class UserDB : UserDao {
    override suspend fun addNewUser(
        name: String,
        email: String,
        phoneNumber: String,
        login: String,
        password: String
    ): UserDBEntry =
        dbQuery {
            UserDBEntry.new {
                this.name = name
                this.email = email
                this.phoneNumber = phoneNumber
                this.login = login
                this.password = password
            }
        }


    override suspend fun getUserByCredentials(credential: UserPasswordCredential): UserDBEntry? =
        dbQuery {
            UserDBEntry.find {
                (Users.login eq credential.name) and (Users.password eq credential.password)
            }.singleOrNull()
        }


    override suspend fun getById(elemId: UserId): UserDBEntry? =
        dbQuery {
            UserDBEntry.findById(elemId)
        }

    override suspend fun deleteById(elemId: UserId) =
        dbQuery {
            UserDBEntry.findById(elemId)?.delete() ?: Unit
        }

    override suspend fun size() = dbQuery { UserDBEntry.all().count() }

    override suspend fun searchByName(name: String): List<UserDBEntry> =
        dbQuery { UserDBEntry.find { Users.name eq name }.toList() }

    override suspend fun getByEmail(email: String) =
        dbQuery { UserDBEntry.find { Users.email eq email }.singleOrNull() }

    override suspend fun getByPhoneNumber(phoneNumber: String) =
        dbQuery { UserDBEntry.find { Users.phoneNumber eq phoneNumber }.singleOrNull() }

    override suspend fun updateName(userId: UserId, newName: String) =
        dbQuery { UserDBEntry.findById(userId)?.name = newName }

    override suspend fun updateEmail(userId: UserId, newEmail: String) =
        dbQuery {
            val user = UserDBEntry.findById(userId)
            if (user != null && getByEmail(newEmail) == null) {
                user.email = newEmail
                true
            } else {
                false
            }
        }

    override suspend fun existsLogin(login: String): Boolean =
        !dbQuery { UserDBEntry.find { Users.login eq login }.empty() }
}

