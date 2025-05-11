package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.useCase.CreateUserUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidUsernameException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateUserUseCaseTest {
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var createUserUseCase: CreateUserUseCase

    private val users = listOf(
        User("testId", "testUsername", "testPassword", UserRole.USER),
        User("testId2", "testUsername2", "testPassword2", UserRole.USER)
    )

    @BeforeEach
    fun setUp() {
        authenticationRepository = mockk()
        createUserUseCase = CreateUserUseCase(authenticationRepository)

    }

    @Test
    fun `should return user data when user enter username and password and not exists before`() = runTest {

        coEvery { authenticationRepository.getAllUsers() } returns users
        coEvery { authenticationRepository.createUser(any(), any()) } returns User("newId", "newTestUsername", "testPassword", UserRole.USER)

        val result = createUserUseCase("newTestUsername", "testPassword")

        assertThat(result.username).isNotIn(users.map { it.username })
    }

    @Test
    fun `should throw exception with type BlankInputException when user not enter username`() = runTest {

        coEvery { authenticationRepository.getAllUsers() } returns users

        assertThrows<BlankInputException> { createUserUseCase("", "testPassword") }
    }

    @Test
    fun `should throw exception with type BlankInputException when user not enter password`() = runTest {

        coEvery { authenticationRepository.getAllUsers() } returns users

        assertThrows<BlankInputException> { createUserUseCase("newTestUsername", "") }
    }

    @Test
    fun `should throw exception with type InvalidUserNameInputException when user enter username with spaces`() = runTest {

        coEvery { authenticationRepository.getAllUsers() } returns users

        assertThrows<InvalidUsernameException> { createUserUseCase("new testUsername", "testPassword") }
    }

}