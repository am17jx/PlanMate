package data.source

import com.google.common.truth.Truth.assertThat
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class CsvAuthenticationDataSourceTest {
    private lateinit var testFile: File
    private lateinit var expectedFile: File
    private lateinit var dataSource: CsvAuthenticationDataSource

    @BeforeEach
    fun setup() {
        testFile = File.createTempFile("test_users", ".csv")
        expectedFile = File.createTempFile("expected_users", ".csv")
        dataSource = CsvAuthenticationDataSource(testFile)
    }

    @AfterEach
    fun deleteFile() {
        if (testFile.exists()) testFile.delete()
        if (expectedFile.exists()) expectedFile.delete()
    }

    @Test
    fun `saveUser should write user to file`() {
        expectedFile.writeText("id,username,password,USER")

        dataSource.saveUser(User("id", "username", "password", UserRole.USER))

        assertThat(testFile.readText()).isEqualTo(expectedFile.readText())
    }

    @Test
    fun `getAllUsers should return all users from file`() {
        testFile.writeText("id,username,password,USER")

        val users = dataSource.getAllUsers()

        assertThat(users[0]).isEqualTo(User("id", "username", "password", UserRole.USER))
    }

}