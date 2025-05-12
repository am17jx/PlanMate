package data.utils.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.source.local.csv.utils.mapper.toCsvRow
import org.example.data.source.local.csv.utils.mapper.toCsvRows
import org.example.data.source.local.csv.utils.mapper.toUsers
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UserCsvMapperKtTest {

    @Test
    fun `toCsvRow should return a CSV row when given a user`() {
        val user = User(Uuid.random(), "usernameTest",UserRole.USER, User.AuthenticationMethod.Password("passwordTest"))
        val expected = "test,usernameTest,passwordTest,USER"

        val result = user.toCsvRow()

        assertThat(result).isEqualTo(expected)

    }

    @Test
    fun `toCsvRows should return a list of CSV rows when given a list of users`() {
        val listOfUsers = listOf(
            User(Uuid.random(), "usernameTest", UserRole.USER, User.AuthenticationMethod.Password("passwordTest")),
            User(Uuid.random(), "usernameTest2", UserRole.USER, User.AuthenticationMethod.Password("passwordTest2"))
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
            User(Uuid.random(), "usernameTest", UserRole.USER, User.AuthenticationMethod.Password("passwordTest")),
            User(Uuid.random(), "usernameTest2", UserRole.USER, User.AuthenticationMethod.Password("passwordTest2"))
        )

        val result = listOfCsvRows.toUsers()

        assertThat(result).isEqualTo(expected)
    }
}