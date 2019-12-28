import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotlintest.matchers.string.shouldNotBeEmpty
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import io.ktor.application.Application
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import org.koin.test.inject
import java.util.*

class KtorServerTest : DBTest() {
    private fun withAppAndServer(test: TestApplicationEngine.(Server) -> Unit) {
        withTestApplication(Application::test) {
            val server: Server by inject()
            test(server)
        }
    }

    private fun TestApplicationEngine.withJsonRequest(uri: String, json: String, code: TestApplicationCall.() -> Unit) {
        with(handleRequest(HttpMethod.Post, uri) {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(json)
        }, code)
    }

    private fun TestApplicationEngine.withLoginRequest(
        username: String,
        password: String,
        code: TestApplicationCall.() -> Unit
    ) {
        withJsonRequest("/login", """{"username": "$username", "password": "$password"}""", code)
    }

    private fun TestApplicationEngine.withRegisterRequest(
        name: String,
        email: String,
        phoneNumber: String,
        username: String,
        password: String,
        code: TestApplicationCall.() -> Unit
    ) {
        withJsonRequest(
            "/register",
            """{"name": "$name", "email": "$email", "phoneNumber": "$phoneNumber", "username": "$username", "password": "$password"}""",
            code
        )
    }

    private fun TestApplicationEngine.withJsonJwtRequest(
        uri: String,
        json: String,
        jwt: String,
        code: TestApplicationCall.() -> Unit
    ) {
        with(handleRequest(HttpMethod.Post, uri) {
            addHeader(HttpHeaders.Authorization, "Bearer $jwt")
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(json)
        }, code)
    }

    @Test
    fun testLoginOK() {
        withAppAndServer { server ->
            server.register("Antoha", "shananton@kek.lol", "+7654382145", "shananton", "qwerty123")
            withLoginRequest("shananton", "qwerty123") {
                response.status() shouldBe HttpStatusCode.OK
            }
        }
    }


    @Test
    fun testLoginFail() {
        withAppAndServer { server ->
            server.register("Antoha", "shananton@kek.lol", "+7654382145", "shananton", "qwerty123")
            withLoginRequest("shananton", "eye4goat") {
                response.status() shouldBe HttpStatusCode.Unauthorized
                response.content shouldBe "Invalid username/password pair"
            }
        }
    }

    @Test
    fun testRegisterOK() {
        withAppAndServer {
            withRegisterRequest("Antoha", "shananton@kek.lol", "+7938529835", "shananton", "qwerty123") {
                response.status() shouldBe HttpStatusCode.OK
            }
        }
    }


    @Test
    fun testRegisterBadEmail() {
        withAppAndServer {
            withRegisterRequest("Antoha", "shananton@@kek.lol", "+7938529835", "shananton", "qwerty123") {
                response.status() shouldBe HttpStatusCode.BadRequest
                response.content shouldBe "Email must contain exactly one '@'"
            }
        }
    }

    @Test
    fun testUseIssuedJwtTokenToAuthorizeRequestOK() {
        withAppAndServer { server ->
            server.register("Antoha", "shananton@kek.lol", "+7654382145", "shananton", "qwerty123")
            var token: String = ""
            withLoginRequest("shananton", "qwerty123") {
                response.status() shouldBe HttpStatusCode.OK
                val content = response.content
                content.shouldNotBeNull()
                content.shouldNotBeEmpty()
                token = content
            }
            withJsonJwtRequest("/getChats", "", token) {
                response.status() shouldBe HttpStatusCode.OK
            }
        }
    }

    @Test
    fun testUseInvalidJwtTokenToAuthorizeFail() {
        withAppAndServer { server ->
            server.register("Antoha", "shananton@kek.lol", "+7654382145", "shananton", "qwerty123")
            var token: String = ""
            withLoginRequest("shananton", "qwerty123") {
                response.status() shouldBe HttpStatusCode.OK
                val content = response.content
                content.shouldNotBeNull()
                content.shouldNotBeEmpty()
                token = content
            }
            withJsonJwtRequest("/getChats", "", token + "1") {
                response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun testUseSelfIssuedJwtTokenOK() {
        withAppAndServer { server ->
            val user = server.register("Antoha", "shananton@kek.lol", "+7654382145", "shananton", "qwerty123")
            withJsonJwtRequest("/getChats", "", JwtConfig.makeToken(user)) {
                response.status() shouldBe HttpStatusCode.OK
            }
        }
    }

    @Test
    fun testUseExpiredJwtTokenFail() {
        withAppAndServer { server ->
            val user = server.register("Antoha", "shananton@kek.lol", "+7654382145", "shananton", "qwerty123")
            withJsonJwtRequest("/getChats", "", JwtConfig.makeToken(user, Date(System.currentTimeMillis() - 1000))) {
                response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun testGetChatMessagesOK() {
        withAppAndServer { server ->
            val user1 = server.register("Antoha", "shananton@kek.lol", "+7654382145", "shananton", "qwerty123")
            val user2 = server.register("Nekit", "bb@aa", "+123456789", "bosow", "password")
            val chat = server.createPersonalChat(user1.id, user2.id)!!
            val msg1 = server.sendMessage(user1.id, true, chat.id, "I'm tired of living")!!
            val msg2 = server.sendMessage(user2.id, true, chat.id, "Hi tired of living, I'm dad")!!
            val mapper = jacksonObjectMapper()
            withJsonJwtRequest(
                "/getChatMessages", mapper.writeValueAsString(
                    GetChatMessagesRequest(chat.id, true)
                ), JwtConfig.makeToken(user1)
            ) {
                response.status() shouldBe HttpStatusCode.OK
                response.content.shouldNotBeNull()
                val resp = mapper.readValue<GetChatMessagesResponse>(response.content!!)
                resp.messages shouldBe listOf(msg1, msg2)
            }
        }
    }


    @Test
    fun testGetChatMessagesForbidden() {
        withAppAndServer { server ->
            val user1 = server.register("Antoha", "shananton@kek.lol", "+7654382145", "shananton", "qwerty123")
            val user2 = server.register("Nekit", "bb@aa", "+123456789", "bosow", "password")
            val user3 = server.register("Eve", "eve@eden", "+228228", "EEE.service", "nandesuka")
            val chat = server.createPersonalChat(user1.id, user2.id)!!
            val msg1 = server.sendMessage(user1.id, true, chat.id, "I'm tired of living")!!
            val msg2 = server.sendMessage(user2.id, true, chat.id, "Hi tired of living, I'm dad")!!
            val mapper = jacksonObjectMapper()
            withJsonJwtRequest(
                "/getChatMessages", mapper.writeValueAsString(
                    GetChatMessagesRequest(chat.id, true)
                ), JwtConfig.makeToken(user3)
            ) {
                response.status() shouldBe HttpStatusCode.Forbidden
                response.content shouldBe "You are not a member of this chat"
            }
        }
    }

//    @Test
//    fun testLogOut() {
//
//    }
}