import dao.BlockedUsersDao
import dao.UserDao
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import org.koin.test.inject

class BlockedUsersDBTest : DBTestWithKoin() {
    @Test
    fun allTest() {
        val base: BlockedUsersDao by inject()
        val users: UserDao by inject()

        val alya = users.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        ).id.value

        val nikita = users.addNewUser(
            "Nikita",
            "Nikita@gmail.com",
            "5553535",
            "Nikita",
            "55555"
        ).id.value

        val antoha = users.addNewUser(
            "Antoha",
            "Antoha@gmail.com",
            "4444444",
            "Kartoha",
            "4444"
        ).id.value

        base.block(alya, nikita)
        base.block(nikita, alya)
        base.block(alya, antoha)
        base.block(antoha, alya)

        base.isBlocked(alya, antoha).shouldBeTrue()
        base.isBlocked(antoha, alya).shouldBeTrue()
        base.isBlocked(alya, nikita).shouldBeTrue()
        base.isBlocked(nikita, alya).shouldBeTrue()

        base.unblock(alya, antoha)
        base.isBlocked(alya, antoha).shouldBeFalse()
        base.isBlocked(antoha, alya).shouldBeTrue()

        base.unblock(alya, nikita)
        base.unblock(alya, nikita)
        base.unblock(antoha, nikita)

        base.block(nikita, antoha)

        base.select(alya).size shouldBe 0
        base.select(antoha).size shouldBe 1
        base.select(nikita).size shouldBe 2
    }
}