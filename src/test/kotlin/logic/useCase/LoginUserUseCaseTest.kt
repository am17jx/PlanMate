package logic.useCase

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
        User("testId", "testUsername", "testPassword", UserRole.USER),
        User("testId2", "testUsername2", "testPassword2", UserRole.USER)
    )

    @BeforeEach
    fun setUp() {
        authenticationRepository = mockk()
        loginUserUseCase = LoginUserUseCase(authenticationRepository)
    }


    @Test
    fun `should return user data when user enter correct username and password`() {

        //Given
        every { authenticationRepository.getAllUsers() } returns users

        //When
        val result = loginUserUseCase("testUsername", "testPassword")

        //Then
        assertEquals(users[0], result)
    }


    @Test
    fun `should throw exception with type BlankInputException when user not enter username`() {

        //Given
        every { authenticationRepository.getAllUsers() } returns users

        // When&Then
        assertThrows<BlankInputException> { loginUserUseCase("", "testPassword") }
    }


    @Test
    fun `should throw exception with type BlankInputException when user not enter password`() {

        //Given
        every { authenticationRepository.getAllUsers() } returns users

        // When&Then
        assertThrows<BlankInputException> { loginUserUseCase("testUsername", "") }
    }

    @Test
    fun `should throw exception with type UserNotFoundException when user enter incorrect username`() {

        //Given
        every { authenticationRepository.getAllUsers() } returns users

        // When&Then
        assertThrows<UserNotFoundException> { loginUserUseCase("incorrectUsername", "testPassword") }
    }

    @Test
    fun `should throw exception with type UserNotFoundException when user enter incorrect password`() {

        //Given
        every { authenticationRepository.getAllUsers() } returns users

        // When&Then
        assertThrows<UserNotFoundException> { loginUserUseCase("testUsername", "incorrectPassword") }
    }
}


