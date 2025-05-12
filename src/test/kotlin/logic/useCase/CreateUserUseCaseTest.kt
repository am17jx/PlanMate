package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.useCase.CreateUserUseCase
import org.example.logic.useCase.Validation
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidUsernameException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateUserUseCaseTest {
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var validation: Validation
    private lateinit var createUserUseCase: CreateUserUseCase
    private val id1 = Uuid.random()
    private val id2 = Uuid.random()

    private val users =
        listOf(
            User(id1, "testUsername", UserRole.USER, User.AuthenticationMethod.Password("testPassword")),
            User(id2, "testUsername2", UserRole.USER, User.AuthenticationMethod.Password("testPassword2")),
        )

    @BeforeEach
    fun setUp() {
        authenticationRepository = mockk()
        validation = mockk()
        createUserUseCase = CreateUserUseCase(authenticationRepository, validation)
    }

    @Test
    fun `should return user data when user enter username and password and not exists before`() =
        runTest {
            coEvery { authenticationRepository.getAllUsers() } returns users
            coEvery { authenticationRepository.createUserWithPassword(any(), any()) } returns
                User(
                    Uuid.random(),
                    "newTestUsername",
                    UserRole.USER,
                    User.AuthenticationMethod.Password("testPassword"),
                )

            val result = createUserUseCase("newTestUsername", "testPassword")

            assertThat(result.username).isNotIn(users.map { it.username })
        }

    @Test
    fun `should throw exception with type BlankInputException when user not enter username`() =
        runTest {
            coEvery { authenticationRepository.getAllUsers() } returns users

            assertThrows<BlankInputException> { createUserUseCase("", "testPassword") }
        }

    @Test
    fun `should throw exception with type BlankInputException when user not enter password`() =
        runTest {
            coEvery { authenticationRepository.getAllUsers() } returns users

            assertThrows<BlankInputException> { createUserUseCase("newTestUsername", "") }
        }

    @Test
    fun `should throw exception with type InvalidUserNameInputException when user enter username with spaces`() =
        runTest {
            coEvery { authenticationRepository.getAllUsers() } returns users

            assertThrows<InvalidUsernameException> { createUserUseCase("new testUsername", "testPassword") }
        }
}
