import dao.UserDao
import entries.UserDBEntry
import io.kotlintest.forOne
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import io.ktor.auth.UserPasswordCredential
import org.koin.test.inject

class UserDbTest : DBTestWithKoin() {

    @Test
    fun getUserByCredentialsTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        val alya2 = base.addNewUser(
            "Alya",
            "Alya2@gmail.com",
            "7654321",
            "Bober",
            "123"
        )

        base.getUserByCredentials(UserPasswordCredential("kek", "lol")).shouldBeNull()
        base.getUserByCredentials(UserPasswordCredential(alya.login, alya.password))?.id shouldBe alya.id
        base.getUserByCredentials(UserPasswordCredential(alya2.login, alya2.password))?.id shouldBe alya2.id
    }

    @Test
    fun getByIdTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        val antoha = base.addNewUser(
            "Antoha",
            "Antoha@gmail.com",
            "4444444",
            "Kartoha",
            "4444"
        )

        base.getById(alya.id.value)?.id shouldBe alya.id
        base.getById(alya.id.value)?.login shouldBe alya.login
        base.getById(antoha.id.value)?.id shouldBe antoha.id
        base.getById(antoha.id.value)?.password shouldBe antoha.password
        base.getById(239).shouldBeNull()

    }

    @Test
    fun deleteByIdTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        val alya2 = base.addNewUser(
            "Alya",
            "Alya2@gmail.com",
            "7654321",
            "Bober",
            "123"
        )
        base.getUserByCredentials(UserPasswordCredential(alya2.login, alya2.password))?.id shouldBe alya2.id

        base.deleteById(alya2.id.value)
        base.deleteById(239)

        base.getUserByCredentials(UserPasswordCredential(alya2.login, alya2.password))?.id.shouldBeNull()
        base.getUserByCredentials(UserPasswordCredential("kek", "lol")).shouldBeNull()
        base.getUserByCredentials(UserPasswordCredential(alya.login, alya.password))?.id shouldBe alya.id
    }

    @Test
    fun sizeTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        val nikita = base.addNewUser(
            "Nikita",
            "Nikita@gmail.com",
            "5553535",
            "Nikita",
            "55555"
        )
        base.size shouldBe 2

        base.deleteById(alya.id.value)

        base.size shouldBe 1

        base.deleteById(nikita.id.value)
        base.deleteById(239)

        base.size shouldBe 0

    }

    @Test
    fun searchByNameTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        val alya2 = base.addNewUser(
            "Alya",
            "Alya2@gmail.com",
            "7654321",
            "Bober",
            "123"
        )

        val vanya = base.addNewUser(
            "Vanya",
            "Vanya@gmail.com",
            "8888888",
            "olivva",
            "8"
        )

        val alyas: List<UserDBEntry> = base.searchByName("Alya")
        val vanyas: List<UserDBEntry> = base.searchByName("Vanya")

        alyas.size shouldBe 2
        forOne(alyas) {
            it.id shouldBe alya.id
        }

        forOne(alyas) {
            it.id shouldBe alya2.id
        }
        vanyas.size shouldBe 1
        vanyas[0].id shouldBe vanya.id

    }

    @Test
    fun getByEmailTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        base.getByEmail("Alya@gmail.com")?.id shouldBe alya.id
        base.getByEmail("kek@gmail.com").shouldBeNull()
    }

    @Test
    fun getByPhoneNumberTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        base.getByPhoneNumber("1234567")?.id shouldBe alya.id
        base.getByPhoneNumber("7654321").shouldBeNull()
    }

    @Test
    fun updateNameTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Sasha",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        val vanya = base.addNewUser(
            "Vanya",
            "Vanya@gmail.com",
            "8888888",
            "olivva",
            "8"
        )

        base.updateName(vanya.id.value, "Sasha")
        base.getById(vanya.id.value)?.name shouldBe "Sasha"

        base.updateName(alya.id.value, "Alya")
        base.updateName(vanya.id.value, "Vanya")

        base.getById(alya.id.value)?.name shouldBe "Alya"
        base.getById(vanya.id.value)?.name shouldBe "Vanya"

    }

    @Test
    fun updateEmailTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        val vanya = base.addNewUser(
            "Vanya",
            "Vanya@gmail.com",
            "8888888",
            "olivva",
            "8"
        )

        val antoha = base.addNewUser(
            "Antoha",
            "Antoha@gmail.com",
            "4444444",
            "Kartoha",
            "4444"
        )

        base.updateEmail(antoha.id.value, "Alya@gmail.com")
        base.updateEmail(alya.id.value, "kek@gmail.com")
        base.updateEmail(vanya.id.value, "Alya@gmail.com")

        base.getById(alya.id.value)?.email shouldBe "kek@gmail.com"
        base.getById(vanya.id.value)?.email shouldBe "Alya@gmail.com"
        base.getById(antoha.id.value)?.email shouldBe "Antoha@gmail.com"
    }

    @Test
    fun existsLoginTest() {

        val base: UserDao by inject()

        val alya = base.addNewUser(
            "Alya",
            "Alya@gmail.com",
            "1234567",
            "Pingwin",
            "123"
        )

        val vanya = base.addNewUser(
            "Vanya",
            "Vanya@gmail.com",
            "8888888",
            "olivva",
            "8"
        )

        base.existsLogin("Pingwin").shouldBeTrue()
        base.existsLogin("olivva").shouldBeTrue()
        base.existsLogin("Bober").shouldBeFalse()
    }
}
