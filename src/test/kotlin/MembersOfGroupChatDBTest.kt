import dao.GroupChatDao
import dao.MembersOfGroupChatDao
import dao.UserDao
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import org.koin.test.inject

class MembersOfGroupChatDBTest : DBTest() {
    @Test
    fun addTest() = withApp {
        val base: MembersOfGroupChatDao by inject()
        val chats: GroupChatDao by inject()
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

        val antoha = users.addNewUser(
            "Antoha",
            "Antoha@gmail.com",
            "4444444",
            "Kartoha",
            "4444"
        ).id.value


        val chat1 = chats.addNewGroupChat(alya, "Alin chat", "knskcsa")?.id?.value
        chat1.shouldNotBeNull()

        val chat2 = chats.addNewGroupChat(vanya, "Vanin chat", "oqlmcznka")?.id?.value
        chat2.shouldNotBeNull()

        base.add(chat1, vanya).shouldBeTrue()
        base.add(chat1, antoha).shouldBeTrue()
        base.add(chat2, alya).shouldBeTrue()
        base.add(239, alya).shouldBeFalse()

    }

    @Test
    fun removeTest() = withApp {
        val base: MembersOfGroupChatDao by inject()
        val chats: GroupChatDao by inject()
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

        val antoha = users.addNewUser(
            "Antoha",
            "Antoha@gmail.com",
            "4444444",
            "Kartoha",
            "4444"
        ).id.value


        val chat1 = chats.addNewGroupChat(alya, "Alin chat", "knskcsa")?.id?.value
        chat1.shouldNotBeNull()


        base.add(chat1, vanya).shouldBeTrue()
        base.add(chat1, antoha).shouldBeTrue()
        base.remove(chat1, vanya).shouldBeTrue()
        base.remove(chat1, vanya).shouldBeFalse()
        base.remove(chat1, alya).shouldBeFalse()
        base.remove(239, alya).shouldBeFalse()
    }

    @Test
    fun selectTest() = withApp {
        val base: MembersOfGroupChatDao by inject()
        val chats: GroupChatDao by inject()
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

        val antoha = users.addNewUser(
            "Antoha",
            "Antoha@gmail.com",
            "4444444",
            "Kartoha",
            "4444"
        ).id.value


        val chat1 = chats.addNewGroupChat(alya, "Alin chat", "knskcsa")?.id?.value
        chat1.shouldNotBeNull()

        val chat2 = chats.addNewGroupChat(vanya, "Vanin chat", "oqlmcznka")?.id?.value
        chat2.shouldNotBeNull()

        base.add(chat1, vanya).shouldBeTrue()
        base.add(chat1, antoha).shouldBeTrue()
        base.add(chat2, alya).shouldBeTrue()

        base.select(chat1).size shouldBe 3
        base.select(chat2).size shouldBe 2

        base.remove(chat1, antoha).shouldBeTrue()

        base.select(chat1).size shouldBe 2

    }

    @Test
    fun containsTest() = withApp {
        val base: MembersOfGroupChatDao by inject()
        val chats: GroupChatDao by inject()
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

        val antoha = users.addNewUser(
            "Antoha",
            "Antoha@gmail.com",
            "4444444",
            "Kartoha",
            "4444"
        ).id.value

        val chat1 = chats.addNewGroupChat(alya, "Alin chat", "knskcsa")?.id?.value
        chat1.shouldNotBeNull()

        val chat2 = chats.addNewGroupChat(vanya, "Vanin chat", "oqlmcznka")?.id?.value
        chat2.shouldNotBeNull()

        base.add(chat1, vanya).shouldBeTrue()
        base.add(chat1, antoha).shouldBeTrue()

        base.contains(chat1, alya).shouldBeTrue()
        base.contains(chat1, vanya).shouldBeTrue()
        base.contains(chat1, antoha).shouldBeTrue()

        base.remove(chat1, vanya).shouldBeTrue()
        base.contains(chat1, vanya).shouldBeFalse()

        base.add(chat2, alya).shouldBeTrue()

        base.contains(chat2, alya).shouldBeTrue()
        base.contains(chat2, vanya).shouldBeTrue()
        base.contains(chat2, antoha).shouldBeFalse()

    }
}