package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.useCase.LoginUserUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.UserNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LoginUserUseCaseTest {
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var loginUserUseCase: LoginUserUseCase

    private val users = listOf(
        User("testId", "testUsername", "fed3b61b26081849378080b34e693d2e", UserRole.USER),
        User("testId2", "testUsername2", "eec4a40174d49402e72d37b79119d0c5", UserRole.USER)
    )

    @BeforeEach
    fun setUp() {
        authenticationRepository = mockk()
        loginUserUseCase = LoginUserUseCase(authenticationRepository)
    }


    @Test
    fun `should return user data when user enter correct username and password`() {

        every { authenticationRepository.getAllUsers() } returns users
        every { authenticationRepository.login(any(), any()) } returns users[0]

        val result = loginUserUseCase("testUsername", "testPassword")

        assertThat(users[0]).isEqualTo(result)
    }


    @Test
    fun `should throw exception with type BlankInputException when user not enter username`() {

        every { authenticationRepository.getAllUsers() } returns users

        assertThrows<BlankInputException> { loginUserUseCase("", "testPassword") }
    }


    @Test
    fun `should throw exception with type BlankInputException when user not enter password`() {

        every { authenticationRepository.getAllUsers() } returns users

        assertThrows<BlankInputException> { loginUserUseCase("testUsername", "") }
    }

    @Test
    fun `should throw exception with type UserNotFoundException when user enter incorrect username`() {

        every { authenticationRepository.getAllUsers() } returns users

        assertThrows<UserNotFoundException> { loginUserUseCase("incorrectUsername", "testPassword") }
    }

    @Test
    fun `should throw exception with type UserNotFoundException when user enter incorrect password`() {

        every { authenticationRepository.getAllUsers() } returns users

        assertThrows<UserNotFoundException> { loginUserUseCase("testUsername", "incorrectPassword") }
    }
}


