package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createUser
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.useCase.LoginUserUseCase
import org.example.logic.useCase.Validation
import org.example.logic.utils.BlankInputException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class LoginUserUseCaseTest {
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var validation: Validation
    private lateinit var loginUserUseCase: LoginUserUseCase
    private val ids = List(6) { Uuid.random() }
    private val users = listOf(
        createUser(ids[0], "testUsername"),
        createUser(ids[1], "testUsername2")
    )

    @BeforeEach
    fun setUp() {
        authenticationRepository = mockk(relaxed = true)
        validation = mockk(relaxed = true)
        loginUserUseCase = LoginUserUseCase(authenticationRepository, validation)
    }


    @Test
    fun `should return user data when user enter username and password that exists in users data`() = runTest {

        coEvery { authenticationRepository.getAllUsers() } returns users
        coEvery { authenticationRepository.loginWithPassword(any(), any()) } returns users[0]

        val result = loginUserUseCase("testUsername", "testPassword")

        assertThat(users[0]).isEqualTo(result)
    }


    @Test
    fun `should throw exception with type BlankInputException when user not enter username`() = runTest {

        coEvery { authenticationRepository.getAllUsers() } returns users

        assertThrows<BlankInputException> { loginUserUseCase("", "testPassword") }
    }


    @Test
    fun `should throw exception with type BlankInputException when user not enter password`() = runTest {

        coEvery { authenticationRepository.getAllUsers() } returns users

        assertThrows<BlankInputException> { loginUserUseCase("testUsername", "") }
    }

}


