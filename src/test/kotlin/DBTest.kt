import io.kotlintest.specs.AnnotationSpec
import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import org.koin.test.KoinTest
import org.testcontainers.containers.PostgreSQLContainer

internal class SpecifiedPostgreSQLContainer(image: String) :
    PostgreSQLContainer<SpecifiedPostgreSQLContainer>(image)

fun Application.test() {
    MessengerApplication(TestDb::init).apply { main() }
}

abstract class DBTest : KoinTest, AnnotationSpec() {


    protected fun withApp(test: TestApplicationEngine.() -> Unit) {
        withTestApplication(Application::test) {
            test()
        }
    }
}