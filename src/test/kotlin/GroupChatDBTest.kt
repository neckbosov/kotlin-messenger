import dao.GroupChatDao
import dao.UserDao
import io.kotlintest.inspectors.forOne
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import org.koin.test.inject

class GroupChatDBTest : ServerTest() {
    @Test
    fun addNewGroupChatTest() = withApp {
        val chats: GroupChatDao by inject()
        val users: UserDao by inject()

        val alya = users.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        ).id.value

        val chat1 = chats.addNewGroupChat(alya, "Alin chat", "knskcsa")?.id?.value
        val chat2 = chats.addNewGroupChat(239, "null", null)?.id?.value

        chat1.shouldNotBeNull()
        chat2.shouldBeNull()
    }

    @Test
    fun getByIdTest() = withApp {
        val chats: GroupChatDao by inject()
        val users: UserDao by inject()

        val alya = users.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        ).id.value

        val chat1 = chats.addNewGroupChat(alya, "Alin chat", "ajhbjakl")?.id?.value
        chat1.shouldNotBeNull()
        chats.getById(chat1)?.id?.value shouldBe chat1
        chats.getById(chat1)?.owner?.value shouldBe alya
        chats.getById(chat1)?.chatName shouldBe "Alin chat"
        chats.getById(239).shouldBeNull()
    }

    @Test
    fun deleteByIdTest() = withApp {
        val chats: GroupChatDao by inject()
        val users: UserDao by inject()

        val alya = users.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        ).id.value

        val chat1 = chats.addNewGroupChat(alya, "Alin chat", "jaknaasx")?.id?.value
        chat1.shouldNotBeNull()

        chats.getById(chat1)?.id?.value shouldBe chat1

        chats.deleteById(chat1)

        chats.getById(chat1).shouldBeNull()

    }

    @Test
    fun sizeTest() = withApp {
        val chats: GroupChatDao by inject()
        val users: UserDao by inject()

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

        val chat1 = chats.addNewGroupChat(vanya, "Vanin chat", "oqlmcznka")?.id?.value
        val chat2 = chats.addNewGroupChat(antoha, "Antohin chat", "nvmbvhajekf")?.id?.value

        chat1.shouldNotBeNull()
        chat2.shouldNotBeNull()

        chats.size shouldBe 2

        chats.deleteById(chat1)

        chats.size shouldBe 1

        chats.deleteById(chat2)

        chats.size shouldBe 0
    }

    @Test
    fun searchByNameTest() = withApp {
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

        val chat1 = chats.addNewGroupChat(alya, "Alin chat", "1")?.id?.value
        val chat2 = chats.addNewGroupChat(alya, "Alin chat", "2")?.id?.value
        val chat3 = chats.addNewGroupChat(vanya, "Vanin chat", "3")?.id?.value

        chats.searchByName("Alin chat").size shouldBe 2
        chats.searchByName("Alin chat").forOne {
            it.uniqueLink shouldBe "1"
        }
        chats.searchByName("Alin chat").forOne {
            it.uniqueLink shouldBe "2"
        }
        chats.searchByName("Vanin chat").size shouldBe 1
        chats.searchByName("Vanin chat").forOne {
            it.uniqueLink shouldBe "3"
        }
        chats.searchByName("Nety(((").shouldBeEmpty()
    }

    @Test
    fun getChatByInviteLinkTest() = withApp {
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

        val chat1 = chats.addNewGroupChat(alya, "Alin chat", "1")?.id?.value
        val chat2 = chats.addNewGroupChat(alya, "Alin chat", "2")?.id?.value
        val chat3 = chats.addNewGroupChat(vanya, "Vanin chat", "3")?.id?.value

        chats.getChatByInviteLink("1")?.id?.value shouldBe chat1
        chats.getChatByInviteLink("2")?.id?.value shouldBe chat2
        chats.getChatByInviteLink("3")?.id?.value shouldBe chat3
        chats.getChatByInviteLink("4").shouldBeNull()
    }
}