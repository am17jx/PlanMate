package data.utils.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.utils.mapper.toCsvRow
import org.example.data.utils.mapper.toCsvRows
import org.example.data.utils.mapper.toUsers
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserCsvMapperKtTest {

    @Test
    fun `toCsvRow should return a CSV row when given a user`() {
        val user = User("test", "usernameTest", "passwordTest",UserRole.USER)
        val expected = "test,usernameTest,passwordTest,USER"

        val result = user.toCsvRow()

        assertThat(result).isEqualTo(expected)

    }

    @Test
    fun `toCsvRows should return a list of CSV rows when given a list of users`() {
        val listOfUsers = listOf(
            User("test", "usernameTest", "passwordTest",UserRole.USER),
            User("test2", "usernameTest2", "passwordTest2",UserRole.USER)
        )

        val expected = listOf(
            "test,usernameTest,passwordTest,USER",
            "test2,usernameTest2,passwordTest2,USER"
        )

        val result = listOfUsers.toCsvRows()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toUsers should return a list of users when given a list of CSV rows`() {
        val listOfCsvRows = listOf(
            "test,usernameTest,passwordTest,USER",
            "test2,usernameTest2,passwordTest2,USER"
        )

        val expected = listOf(
            User("test", "usernameTest", "passwordTest",UserRole.USER),
            User("test2", "usernameTest2", "passwordTest2",UserRole.USER)
        )

        val result = listOfCsvRows.toUsers()

        assertThat(result).isEqualTo(expected)
    }
}