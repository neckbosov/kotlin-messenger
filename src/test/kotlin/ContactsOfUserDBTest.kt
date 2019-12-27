import dao.ContactsOfUserDao
import dao.UserDao
import io.kotlintest.inspectors.forNone
import io.kotlintest.inspectors.forOne
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.shouldBe
import org.koin.test.inject

class ContactsOfUserDBTest : DBTestWithKoin() {
    @Test
    fun addAndContainsTest() {
        val base: ContactsOfUserDao by inject()
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

        base.add(alya, Pair(nikita, "Nikita")).shouldBeTrue()
        base.add(alya, Pair(antoha, "Kartoha")).shouldBeTrue()
        base.add(antoha, Pair(alya, "kek")).shouldBeTrue()

        base.contains(alya, Pair(nikita, "Nikita")).shouldBeTrue()
        base.contains(alya, Pair(antoha, "Kartoha")).shouldBeTrue()
        base.contains(antoha, Pair(alya, "kek")).shouldBeTrue()

        base.contains(alya, Pair(nikita, "Bosov")).shouldBeTrue()
        base.contains(alya, Pair(antoha, "KOKOKOKOKOKOKOKOKO")).shouldBeTrue()

        base.contains(nikita, Pair(alya, "kek")).shouldBeFalse()
    }

    @Test
    fun removeAndSelectTest() {
        val base: ContactsOfUserDao by inject()
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

        base.add(alya, Pair(nikita, "Nikita")).shouldBeTrue()
        base.add(alya, Pair(antoha, "Kartoha")).shouldBeTrue()
        base.add(antoha, Pair(alya, "kek")).shouldBeTrue()

        base.select(alya).size shouldBe 2
        base.select(alya).forOne {
            it.first shouldBe antoha
        }
        base.select(alya).forOne {
            it.first shouldBe nikita
        }
        base.select(antoha).size shouldBe 1
        base.select(antoha).forOne {
            it.first shouldBe alya
        }
        base.select(nikita).shouldBeEmpty()


        base.contains(alya, Pair(nikita, "Nikita")).shouldBeTrue()
        base.contains(alya, Pair(antoha, "Kartoha")).shouldBeTrue()
        base.remove(alya, Pair(nikita, "Nikita")).shouldBeTrue()
        base.remove(alya, Pair(antoha, "KOKOKOKOKOKOKOKOKO")).shouldBeTrue()

        base.contains(alya, Pair(nikita, "Nikita")).shouldBeFalse()
        base.contains(alya, Pair(antoha, "Kartoha")).shouldBeFalse()

        base.select(alya).shouldBeEmpty()
        base.select(antoha).size shouldBe 1
        base.select(nikita).shouldBeEmpty()
    }

    @Test
    fun changeNameTest() {
        val base: ContactsOfUserDao by inject()
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

        base.add(alya, Pair(nikita, "Nikita")).shouldBeTrue()
        base.add(alya, Pair(antoha, "Kartoha")).shouldBeTrue()
        base.add(antoha, Pair(alya, "kek")).shouldBeTrue()

        base.changeName(alya, antoha, "Antoha")
        base.changeName(antoha, alya, "Alya")

        base.select(alya).forOne {
            it.second shouldBe "Antoha"
        }
        base.select(alya).forNone {
            it.second shouldBe "Kartoha"
        }
        base.select(antoha).forOne {
            it.second shouldBe "Alya"
        }
        base.select(antoha).forNone {
            it.second shouldBe "kek"
        }
    }
}