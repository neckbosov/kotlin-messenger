import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import tables.*

internal class SpecifiedPostgreSQLContainer(image: String) :
    PostgreSQLContainer<SpecifiedPostgreSQLContainer>(image)

@Testcontainers
object TestDb {
    @Container
    @JvmField
    internal val postgres = PostgreSQLContainer<SpecifiedPostgreSQLContainer>()

    fun init() {
        postgres.start()
        Database.connect(
            postgres.jdbcUrl, driver = "org.postgresql.Driver",
            user = postgres.username, password = postgres.password
        )
        transaction {
            SchemaUtils.drop(
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
}