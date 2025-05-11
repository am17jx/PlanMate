package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.repository.AuthenticationRepositoryImpl
import org.example.data.repository.utils.hashWithMD5
import org.example.data.repository.sources.remote.RemoteAuthenticationDataSource
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthenticationRepositoryImplTest {
    private lateinit var remoteAuthenticationDataSource: RemoteAuthenticationDataSource
    private lateinit var authenticationRepository: AuthenticationRepositoryImpl

    private val users =
        listOf(
            User("testId", "testUsername", "testPassword", UserRole.USER),
            User("testId2", "testUsername2", "testPassword2", UserRole.USER),
        )
    private val testUsername = "testUsername"
    private val testPassword = "testPassword"

    @BeforeEach
    fun setUp() {
        remoteAuthenticationDataSource = mockk(relaxed = true)
        authenticationRepository = AuthenticationRepositoryImpl(remoteAuthenticationDataSource)
    }

    @Test
    fun `getCurrentUser should return logged in user when user is logged in`() =
        runTest {
            coEvery { remoteAuthenticationDataSource.getCurrentUser() } returns users.first()

            val result = authenticationRepository.getCurrentUser()

            assertThat(result).isEqualTo(users.first())
        }

    @Test
    fun `login should set and return the current user when user is logged in`() =
        runTest {
            coEvery { remoteAuthenticationDataSource.login(any(), any()) } returns users.first()

            val loggedInUser = authenticationRepository.login(testUsername, testPassword)

            assertThat(loggedInUser.username).isEqualTo(testUsername)
            assertThat(loggedInUser.password).isEqualTo(testPassword)
            assertThat(loggedInUser.role).isEqualTo(UserRole.USER)
        }

    @Test
    fun `createMate should save and return the created user when create new mate`() =
        runTest {
            coEvery { remoteAuthenticationDataSource.saveUser(any()) } returns Unit

            val createdUser = authenticationRepository.createUser(testUsername, testPassword)

            coVerify { remoteAuthenticationDataSource.saveUser(any()) }
            assertThat(createdUser.username).isEqualTo(testUsername)
            assertThat(createdUser.password).isEqualTo(hashWithMD5(testPassword))
            assertThat(createdUser.role).isEqualTo(UserRole.USER)
        }

    @Test
    fun `getAllUsers should call getAllUsers function from remoteAuthenticationDataSource when get all users`() =
        runTest {
            coEvery { remoteAuthenticationDataSource.getAllUsers() } returns users

            val result = authenticationRepository.getAllUsers()

            assertThat(result).isEqualTo(users)
        }

    @Test
    fun `logout should call logout function from remoteAuthenticationDataSource when logout`() =
        runTest {
            authenticationRepository.logout()

            coVerify { remoteAuthenticationDataSource.logout() }
        }
}
