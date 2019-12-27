import dao.GroupChatDao
import dao.GroupChatsOfUserDao
import dao.UserDao
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import org.koin.test.inject

class GroupChatOfUserDBTest : DBTestWithKoin() {
    @Test
    fun addTest() {
        val base: GroupChatsOfUserDao by inject()
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
        val chat2 = chats.addNewGroupChat(vanya, "Vanin chat", "oqlmcznka")?.id?.value

        chat1.shouldNotBeNull()
        chat2.shouldNotBeNull()

        base.add(vanya, chat1).shouldBeTrue()
        base.add(antoha, chat1).shouldBeTrue()
        base.add(alya, chat2).shouldBeTrue()
        base.add(alya, 239).shouldBeFalse()
    }

    @Test
    fun removeTest() {
        val base: GroupChatsOfUserDao by inject()
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

        base.add(vanya, chat1).shouldBeTrue()
        base.add(antoha, chat1).shouldBeTrue()

        base.remove(vanya, chat1).shouldBeTrue()
        base.remove(vanya, chat1).shouldBeFalse()
        base.remove(alya, chat1).shouldBeFalse()
        base.remove(alya, 239).shouldBeFalse()
    }

    @Test
    fun selectTest() {
        val base: GroupChatsOfUserDao by inject()
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
        val chat2 = chats.addNewGroupChat(vanya, "Vanin chat", "oqlmcznka")?.id?.value

        chat1.shouldNotBeNull()
        chat2.shouldNotBeNull()

        base.add(vanya, chat1).shouldBeTrue()
        base.add(antoha, chat1).shouldBeTrue()
        base.add(alya, chat2).shouldBeTrue()

        base.select(alya).size shouldBe 2
        base.select(vanya).size shouldBe 2
        base.select(antoha).size shouldBe 1

        base.remove(antoha, chat1).shouldBeTrue()

        base.select(antoha).shouldBeEmpty()

    }

    @Test
    fun containsTest() {
        val base: GroupChatsOfUserDao by inject()
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
        val chat2 = chats.addNewGroupChat(vanya, "Vanin chat", "oqlmcznka")?.id?.value

        chat1.shouldNotBeNull()
        chat2.shouldNotBeNull()

        base.add(vanya, chat1).shouldBeTrue()
        base.add(antoha, chat1).shouldBeTrue()

        base.contains(alya, chat1).shouldBeTrue()
        base.contains(vanya, chat1).shouldBeTrue()
        base.contains(antoha, chat1).shouldBeTrue()

        base.remove(vanya, chat1).shouldBeTrue()
        base.contains(vanya, chat1).shouldBeFalse()

        base.add(alya, chat2).shouldBeTrue()

        base.contains(alya, chat2).shouldBeTrue()
        base.contains(vanya, chat2).shouldBeTrue()

        base.contains(antoha, chat2).shouldBeFalse()

    }
}