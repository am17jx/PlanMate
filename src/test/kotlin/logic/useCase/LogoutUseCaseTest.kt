package logic.useCase

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.useCase.LogoutUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LogoutUseCaseTest {
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var logoutUseCase: LogoutUseCase

    @BeforeEach
    fun setUp() {
        authenticationRepository = mockk(relaxed = true)
        logoutUseCase = LogoutUseCase(authenticationRepository)
    }

    @Test
    fun `should call logout on authentication repository when useCase invoked`() =
        runTest {
            logoutUseCase()

            coVerify { authenticationRepository.logout() }
        }
}
