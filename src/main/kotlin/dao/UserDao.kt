package dao

import entries.UserDBEntry
import io.ktor.auth.UserPasswordCredential

typealias UserId = Long

interface UserDao : ObjectDao<UserDBEntry> {
    suspend fun addNewUser(
        name: String,
        email: String,
        phoneNumber: String,
        login: String,
        password: String
    ): UserDBEntry

    suspend fun searchByName(name: String): List<UserDBEntry>
    suspend fun getByEmail(email: String): UserDBEntry?
    suspend fun getByPhoneNumber(phoneNumber: String): UserDBEntry?
    suspend fun getUserByCredentials(credential: UserPasswordCredential): UserDBEntry?
    suspend fun updateName(userId: UserId, newName: String)
    suspend fun updateEmail(userId: UserId, newEmail: String): Boolean
    suspend fun existsLogin(login: String): Boolean
}
