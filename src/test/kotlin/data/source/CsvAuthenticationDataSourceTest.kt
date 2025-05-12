package data.source

import com.google.common.truth.Truth.assertThat
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

class CsvAuthenticationDataSourceTest {
    private lateinit var testFile: File
    private lateinit var expectedFile: File
    private lateinit var dataSource: CsvAuthenticationDataSource


    private val user = User("testId", "testUsername", "fed3b61b26081849378080b34e693d2e", UserRole.USER)
    private val testUsername = "testUsername"
    private val testHashedPassword = "fed3b61b26081849378080b34e693d2e"

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
        expectedFile.writeText("id,username,password,USER")

        dataSource.saveUser(User("id", "username", "password", UserRole.USER))

        assertThat(testFile.readText()).isEqualTo(expectedFile.readText())
    }

    @Test
    fun `saveUser should throw exception with type UserAlreadyExistsException when user enter username is exists before`() {

        testFile.writeText("id,testUsername,password,USER")

        assertThrows<UserAlreadyExistsException> {
            dataSource.saveUser(
                User(
                    "testId",
                    "testUsername",
                    "password",
                    UserRole.USER
                )
            )
        }
    }


    @Test
    fun `getAllUsers should return all users from file`() {
        testFile.writeText("id,username,password,USER")
        val expectedUser = User("id", "username", "password", UserRole.USER)

        val users = dataSource.getAllUsers()

        assertThat(users[0]).isEqualTo(expectedUser)
    }

    @Test
    fun `login should return user data when user enter username and password that exists in users data`() {
        testFile.writeText("testId,testUsername,fed3b61b26081849378080b34e693d2e,USER")

        val result = dataSource.loginWithPassword(testUsername, testHashedPassword)

        assertThat(user).isEqualTo(result)
    }

    @Test
    fun `getCurrentUser should return logged in user when user is logged in`() {
        testFile.writeText("testId,testUsername,fed3b61b26081849378080b34e693d2e,USER")
        dataSource.loginWithPassword(testUsername, testHashedPassword)

        val result = dataSource.getCurrentUser()

        assertThat(result).isEqualTo(user)
    }

    @Test
    fun `getCurrentUser should return null when user is not logged in`() {
        val result = dataSource.getCurrentUser()

        assertThat(result).isNull()
    }

}