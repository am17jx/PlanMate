package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.utils.NoLoggedInUserException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetCurrentUserUseCaseTest {
    private lateinit var authRepository: AuthenticationRepository
    private lateinit var getCurrentUserUseCaseTest: GetCurrentUserUseCase

    @BeforeEach
    fun setUp() {
        authRepository = mockk(relaxed = true)
        getCurrentUserUseCaseTest = GetCurrentUserUseCase(authRepository)
    }

    @Test
    fun `should return current user when user is logged in`() =
        runTest {
            val user =
                User(Uuid.random(), "fares ", UserRole.USER, User.AuthenticationMethod.Password("f4556fd41d3s964s"))
            coEvery { authRepository.getCurrentUser() } returns user

            val result = getCurrentUserUseCaseTest()

            assertThat(result).isEqualTo(user)
        }


    @Test
    fun `should throw NoLoggedInUserException when user is not logged in`() =
        runTest {
            val user = null
            coEvery { authRepository.getCurrentUser() } returns user

            assertThrows<NoLoggedInUserException> {
                getCurrentUserUseCaseTest()
            }
        }
}
