import dao.GroupChatDao
import dao.MessageDao
import dao.PersonalChatDao
import dao.UserDao
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import org.koin.test.inject

class MessageDBTest : DBTestWithKoin() {
    @Test
    fun getByIdTest() {
        val base: MessageDao by inject()
        val pchats: PersonalChatDao by inject()
        val gchats: GroupChatDao by inject()
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

        val chat1 = pchats.addNewPersonalChat(alya, vanya)
        chat1.shouldNotBeNull()

        val chat2 = gchats.addNewGroupChat(alya, "Alin chat", "1")
        chat2.shouldNotBeNull()

        val msg1 = base.addNewMessage(alya, true, chat1.id.value, "Priv")
        msg1.shouldNotBeNull()

        val msg2 = base.addNewMessage(alya, false, chat2.id.value, "Vsem privet!")
        msg2.shouldNotBeNull()

        base.getById(msg1.id.value)?.id?.value shouldBe msg1.id.value
        msg1.chat shouldBe chat1.id.value
        msg1.isPersonal.shouldBeTrue()

        base.getById(msg2.id.value)?.id?.value shouldBe msg2.id.value
        msg2.chat shouldBe chat2.id.value
        msg2.isPersonal.shouldBeFalse()
    }

    @Test
    fun deleteByIdTest() {
        val base: MessageDao by inject()
        val pchats: PersonalChatDao by inject()
        val gchats: GroupChatDao by inject()
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

        val chat1 = pchats.addNewPersonalChat(alya, vanya)
        chat1.shouldNotBeNull()

        val chat2 = gchats.addNewGroupChat(alya, "Alin chat", "1")
        chat2.shouldNotBeNull()

        val msg1 = base.addNewMessage(alya, true, chat1.id.value, "Priv")
        msg1.shouldNotBeNull()

        val msg2 = base.addNewMessage(alya, false, chat2.id.value, "Vsem privet!")
        msg2.shouldNotBeNull()

        base.getById(msg1.id.value)?.id?.value shouldBe msg1.id.value

        base.deleteById(msg1.id.value)

        base.getById(msg1.id.value).shouldBeNull()

        base.getById(msg2.id.value)?.id?.value shouldBe msg2.id.value

        base.deleteById(msg2.id.value)

        base.getById(msg2.id.value).shouldBeNull()
    }

    @Test
    fun findByUserAndfindSliceFromChatAndSizeTest() {
        val base: MessageDao by inject()
        val pchats: PersonalChatDao by inject()
        val gchats: GroupChatDao by inject()
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

        val chat1 = pchats.addNewPersonalChat(alya, vanya)
        chat1.shouldNotBeNull()

        val chat2 = gchats.addNewGroupChat(alya, "Alin chat", "1")
        chat2.shouldNotBeNull()

        val msg1 = base.addNewMessage(alya, true, chat1.id.value, "Priv")
        msg1.shouldNotBeNull()

        base.size shouldBe 1

        val msg2 = base.addNewMessage(alya, false, chat2.id.value, "Vsem privet!")

        base.size shouldBe 2

        val msg3 = base.addNewMessage(alya, false, chat2.id.value, "Alya is cool!")
        val msg4 = base.addNewMessage(alya, false, chat2.id.value, "Kotlin top!")

        base.size shouldBe 4
        base.findByUser(alya).size shouldBe 4
        base.findByUser(vanya).size shouldBe 0

//        if (chat1?.id?.value != null) {
//            Assertions.assertEquals(0, base.findSliceFromChat(true, chat1.id.value, 0, 1).size)
//            Assertions.assertEquals(1, base.findSliceFromChat(true, chat1.id.value, 1, 1).size)
//        }
//
//        if (chat2?.id?.value != null) {
//            Assertions.assertEquals(3, base.findSliceFromChat(false, chat2.id.value, 3, 0).size)
//            Assertions.assertEquals(2, base.findSliceFromChat(false, chat2.id.value, 2, 1).size)
//        }
    }

}