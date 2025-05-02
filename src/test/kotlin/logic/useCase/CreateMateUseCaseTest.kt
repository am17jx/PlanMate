package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.useCase.CreateMateUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidUserNameInputException
import org.example.logic.utils.UserAlreadyExistsException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateMateUseCaseTest {
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var createUserUseCase: CreateMateUseCase

    private val users = listOf(
        User("testId", "testUsername", "testPassword", UserRole.USER),
        User("testId2", "testUsername2", "testPassword2", UserRole.USER)
    )

    @BeforeEach
    fun setUp() {
        authenticationRepository = mockk()
        createUserUseCase = CreateMateUseCase(authenticationRepository)

    }

    @Test
    fun `should return user data when user enter username and password and not exists before`() {

        every { authenticationRepository.getAllUsers() } returns users
        every { authenticationRepository.createMate(any(), any()) } returns User("newId", "newTestUsername", "testPassword", UserRole.USER)

        val result = createUserUseCase("newTestUsername", "testPassword")

        assertThat(result.username).isNotIn(users.map { it.username })
    }

    @Test
    fun `should throw exception with type UserAlreadyExistsException when user enter username is exists before`() {

        every { authenticationRepository.getAllUsers() } returns users

        assertThrows<UserAlreadyExistsException> { createUserUseCase("testUsername", "testPassword") }
    }

    @Test
    fun `should throw exception with type BlankInputException when user not enter username`() {

        every { authenticationRepository.getAllUsers() } returns users

        assertThrows<BlankInputException> { createUserUseCase("", "testPassword") }
    }

    @Test
    fun `should throw exception with type BlankInputException when user not enter password`() {

        every { authenticationRepository.getAllUsers() } returns users

        assertThrows<BlankInputException> { createUserUseCase("newTestUsername", "") }
    }

    @Test
    fun `should throw exception with type InvalidUserNameInputException when user enter username with spaces`() {

        every { authenticationRepository.getAllUsers() } returns users

        assertThrows<InvalidUserNameInputException> { createUserUseCase("new testUsername", "testPassword") }
    }

}