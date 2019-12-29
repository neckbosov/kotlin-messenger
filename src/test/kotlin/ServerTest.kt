import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.specs.AnnotationSpec
import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.test.KoinTest
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import tables.*

internal class SpecifiedPostgreSQLContainer(image: String) :
    PostgreSQLContainer<SpecifiedPostgreSQLContainer>(image)

@Testcontainers
abstract class ServerTest : KoinTest, AnnotationSpec() {
    @Container
    @JvmField
    internal val postgres = PostgreSQLContainer<SpecifiedPostgreSQLContainer>()

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        postgres.start()
        Database.connect(
            postgres.jdbcUrl, driver = "org.postgresql.Driver",
            user = postgres.username, password = postgres.password
        )
        transaction {
            SchemaUtils.create(
                BlockedUsers,
                Contacts,
                GroupChatsToUsers,
                GroupChats,
                Messages,
                PersonalChats,
                Users
            )
        }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        postgres.stop()
    }

    override fun beforeTest(testCase: TestCase) = runBlocking {
        newSuspendedTransaction {
            BlockedUsers.deleteAll()
            Contacts.deleteAll()
            GroupChatsToUsers.deleteAll()
            GroupChats.deleteAll()
            Messages.deleteAll()
            PersonalChats.deleteAll()
            Users.deleteAll()
            Unit
        }
    }

    protected fun withApp(test: suspend TestApplicationEngine.() -> Unit) {
        withTestApplication(Application::main) {
            runBlocking {
                test()
            }
        }
    }
}