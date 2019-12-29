import dao.*
import io.ktor.auth.UserPasswordCredential
import org.koin.core.KoinComponent
import org.koin.core.inject

data class InvalidRequestException(val reason: String) : Exception()

class Server : KoinComponent {
    val userBase: UserDao by inject()
    val personalChatBase: PersonalChatDao by inject()
    val groupChatBase: GroupChatDao by inject()
    val messageBase: MessageDao by inject()
    val blockedUsersBase: BlockedUsersDao by inject()
    val groupChatsOfUserBase: GroupChatsOfUserDao by inject()
    val membersOfGroupChatBase: MembersOfGroupChatDao by inject()
    val contactsOfUserBase: ContactsOfUserDao by inject()

    suspend fun register(name: String, email: String, phoneNumber: String, login: String, password: String): User {
        return when {
            email.count { it == '@' } != 1 -> throw InvalidRequestException("Email must contain exactly one '@'")
            !phoneNumber.all {
                it in listOf(
                    '+',
                    '-',
                    '(',
                    ')'
                ) || it.isDigit()
            } -> throw InvalidRequestException("Invalid phone number")
            userBase.existsLogin(login) -> throw InvalidRequestException("Username already taken")
            else -> userBase.addNewUser(name, email, phoneNumber, login, password).toUser()
        }
    }

    suspend fun getUserByCredentials(credentials: UserPasswordCredential): User? =
        userBase.getUserByCredentials(credentials)?.toUser()

    suspend fun getUserById(id: UserId): User? = userBase.getById(id)?.toUser()

//    fun isUserMemberOfChat(id: UserId, chatId: )

    suspend fun getChats(userId: UserId): List<GroupChatId> = getPersonalChats(userId).plus(getGroupChats(userId))
    suspend fun getPersonalChats(userId: UserId) = personalChatBase.selectWithUser(userId)

    suspend fun getGroupChats(userId: UserId) = groupChatsOfUserBase.select(userId)

    suspend fun getContacts(userId: UserId) = contactsOfUserBase.select(userId)

    suspend fun getChatMessages(chatId: Id, isPersonal: Boolean): List<Message> =
        messageBase.getMessagesFromChat(isPersonal, chatId).map { it.toMessage() }

    suspend fun sendMessage(from: UserId, isPersonal: Boolean, chatId: Id, text: String) =
        messageBase.addNewMessage(from, isPersonal, chatId, text)?.toMessage()

    suspend fun createGroupChat(userId: UserId, name: String, uniqueLink: String?) =
        groupChatBase.addNewGroupChat(userId, name, uniqueLink)?.toGroupChat()

    suspend fun createPersonalChat(user1: UserId, user2: UserId) =
        personalChatBase.addNewPersonalChat(user1, user2)?.toPersonalChat()
}
