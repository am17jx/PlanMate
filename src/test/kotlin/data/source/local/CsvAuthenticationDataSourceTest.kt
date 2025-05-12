package data.source.local

import com.google.common.truth.Truth
import org.example.data.source.local.csv.CsvAuthenticationDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.utils.UserAlreadyExistsException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CsvAuthenticationDataSourceTest {
    private lateinit var testFile: File
    private lateinit var expectedFile: File
    private lateinit var dataSource: CsvAuthenticationDataSource

    private val testUsername = "username"
    private val testPassword = "password"
    private val userId = Uuid.random()
    private val user = User(userId, testUsername, UserRole.USER, User.AuthenticationMethod.Password(testPassword))

    @BeforeEach
    fun setup() {
        testFile = File.createTempFile("test_users", ".csv")
        expectedFile = File.createTempFile("expected_users", ".csv")
        dataSource = CsvAuthenticationDataSource(CSVWriter(testFile), CSVReader(testFile))
    }

    @AfterEach
    fun deleteFile() {
        if (testFile.exists()) testFile.delete()
        if (expectedFile.exists()) expectedFile.delete()
    }

    @Test
    fun `saveUser should write user to file when user not exists`() {
        expectedFile.writeText("${userId.toHexString()},username,USER,PASSWORD,password")

        dataSource.saveUser(User(userId, "username", UserRole.USER, User.AuthenticationMethod.Password("password")))

        Truth.assertThat(testFile.readText()).isEqualTo(expectedFile.readText())
    }

    @Test
    fun `saveUser should throw exception with type UserAlreadyExistsException when user enter username is exists before`() {
        testFile.writeText("${userId.toHexString()},testUsername,USER,PASSWORD,password")

        assertThrows<UserAlreadyExistsException> {
            dataSource.saveUser(
                User(
                    Uuid.random(),
                    "testUsername",
                    UserRole.USER,
                    User.AuthenticationMethod.Password("password")
                ),
            )
        }
    }

    @Test
    fun `getAllUsers should return all users from file`() {
        testFile.writeText("${userId.toHexString()},username,USER,PASSWORD,password")
        val expectedUser = User(userId, "username", UserRole.USER, User.AuthenticationMethod.Password("password"))

        val users = dataSource.getAllUsers()

        Truth.assertThat(users[0]).isEqualTo(expectedUser)
    }

    @Test
    fun `login should return user data when user enter username and password that exists in users data`() {
        testFile.writeText("${userId.toHexString()},username,USER,PASSWORD,password")

        val result = dataSource.loginWithPassword(testUsername, testPassword)

        Truth.assertThat(user).isEqualTo(result)
    }

    @Test
    fun `getCurrentUser should return logged in user when user is logged in`() {
        testFile.writeText("${userId.toHexString()},username,USER,PASSWORD,password")
        dataSource.loginWithPassword(testUsername, testPassword)

        val result = dataSource.getCurrentUser()

        Truth.assertThat(result).isEqualTo(user)
    }

    @Test
    fun `getCurrentUser should return null when user is not logged in`() {
        val result = dataSource.getCurrentUser()

        Truth.assertThat(result).isNull()
    }
}
