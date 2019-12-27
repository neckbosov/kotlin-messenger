import dao.PersonalChatDao
import dao.UserDao
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import org.koin.test.inject

class PersonalChatTests : DBTestWithKoin() {
    @Test
    fun addNewPersonalChatTest() {
        val chats: PersonalChatDao by inject()
        val users: UserDao by inject()

        val alya = users.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        ).id.value

        val vanya = users.addNewUser(
            "Vanya",
            "Vanya@gmail.com",
            "8888888",
            "olivva",
            "8"
        ).id.value

        val chat1 = chats.addNewPersonalChat(alya, vanya)?.id?.value
        val chat2 = chats.addNewPersonalChat(239, 30)?.id?.value
        val chat3 = chats.addNewPersonalChat(alya, 239)?.id?.value

        chat1.shouldNotBeNull()
        chat2.shouldBeNull()
        chat3.shouldBeNull()
    }

    @Test
    fun getByIdTest() {
        val chats: PersonalChatDao by inject()
        val users: UserDao by inject()

        val alya = users.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        ).id.value

        val vanya = users.addNewUser(
            "Vanya",
            "Vanya@gmail.com",
            "8888888",
            "olivva",
            "8"
        ).id.value

        val chat1 = chats.addNewPersonalChat(alya, vanya)?.id?.value
        chat1.shouldNotBeNull()
        chats.getById(chat1)?.id?.value shouldBe chat1
        chats.getById(chat1)?.member1?.value shouldBe alya
        chats.getById(chat1)?.member2?.value shouldBe vanya
        chats.getById(239).shouldBeNull()
    }

    @Test
    fun deleteByIdTest() {
        val chats: PersonalChatDao by inject()
        val users: UserDao by inject()

        val alya = users.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        ).id.value

        val vanya = users.addNewUser(
            "Vanya",
            "Vanya@gmail.com",
            "8888888",
            "olivva",
            "8"
        ).id.value

        val chat1 = chats.addNewPersonalChat(alya, vanya)?.id?.value

        chat1.shouldNotBeNull()
        chats.getById(chat1)?.id?.value shouldBe chat1

        chats.deleteById(chat1)

        chats.getById(chat1)?.id.shouldBeNull()
    }

    @Test
    fun sizeTest() {
        val chats: PersonalChatDao by inject()
        val users: UserDao by inject()

        val nikita = users.addNewUser(
            "Nikita",
            "Nikita@gmail.com",
            "5553535",
            "Nikita",
            "55555"
        ).id.value

        val vanya = users.addNewUser(
            "Vanya",
            "Vanya@gmail.com",
            "8888888",
            "olivva",
            "8"
        ).id.value

        val antoha = users.addNewUser(
            "Antoha",
            "Antoha@gmail.com",
            "4444444",
            "Kartoha",
            "4444"
        ).id.value

        val chat1 = chats.addNewPersonalChat(nikita, vanya)?.id?.value
        val chat2 = chats.addNewPersonalChat(nikita, antoha)?.id?.value
        val chat3 = chats.addNewPersonalChat(antoha, vanya)?.id?.value

        chat1.shouldNotBeNull()
        chat2.shouldNotBeNull()
        chat3.shouldNotBeNull()
        chats.size shouldBe 3

        chats.deleteById(chat1)

        chats.size shouldBe 2

        chats.deleteById(chat2)

        chats.size shouldBe 1

        chats.deleteById(chat3)

        chats.size shouldBe 0
    }
}