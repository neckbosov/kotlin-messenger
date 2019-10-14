import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GroupChatTest {
    val base = MessageDB

    @Test
    fun testAddUserPublicChat() {
        val chat = GroupChat(base, 0, "kek", null)
        Assertions.assertEquals(chat.users.size, 1)
        Assertions.assertTrue(chat.addUser(1, null))
        Assertions.assertEquals(chat.users.size, 2)
        Assertions.assertFalse(chat.addUser(1, null))
        Assertions.assertEquals(chat.users.size, 2)
    }

    @Test
    fun testAddUserPrivateChat() {
        val chat = GroupChat(base, 0, "lol", "sm.me/lolabacabadaba")
        Assertions.assertEquals(chat.users.size, 1)
        Assertions.assertFalse(chat.addUser(1, null))
        Assertions.assertEquals(chat.users.size, 1)
        Assertions.assertTrue(chat.addUser(2, "sm.me/lolabacabadaba"))
        Assertions.assertEquals(chat.users.size, 2)
        Assertions.assertFalse(chat.addUser(3, "t.me/lolabacabadaba"))
        Assertions.assertEquals(chat.users.size, 2)
        Assertions.assertFalse(chat.addUser(2, "sm.me/lolabacabadaba"))
        Assertions.assertEquals(chat.users.size, 2)
    }
    
    @Test
    fun testLeave() {
        val chat = GroupChat(base, 0, "kek", null)
        chat.addUser(1)
        Assertions.assertEquals(chat.users.size, 2)
        chat.leave(1)
        Assertions.assertEquals(chat.users.size, 1)
    }
}
