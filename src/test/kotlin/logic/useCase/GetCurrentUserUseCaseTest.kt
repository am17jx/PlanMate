package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.utils.NoLoggedInUserException
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetCurrentUserUseCaseTest {
    private lateinit var authRepository: AuthenticationRepository
    private lateinit var getCurrentUserUseCaseTest: GetCurrentUserUseCase

    @BeforeEach
    fun setUp() {
        authRepository = mockk(relaxed = true)
        getCurrentUserUseCaseTest= GetCurrentUserUseCase(authRepository)
    }

    @Test
    fun `should return current user when user is logged in`(){
        val user = User("1","fares","f4556fd41d3s964s",UserRole.USER)
        every { authRepository.getCurrentUser() } returns user

        val result = getCurrentUserUseCaseTest()

        assertThat(result).isEqualTo(user)
    }

    @Test
    fun `should throw NoLoggedInUserException when user is not logged in`(){
        val user =null
        every { authRepository.getCurrentUser() } returns user

        assertThrows<NoLoggedInUserException> {
            getCurrentUserUseCaseTest()
        }
    }

}