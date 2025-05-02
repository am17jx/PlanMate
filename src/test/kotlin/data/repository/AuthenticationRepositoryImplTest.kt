package data.repository

import com.google.common.truth.Truth.assertThat
import data.source.local.contract.LocalAuthenticationDataSource
import io.mockk.*
import org.example.data.repository.AuthenticationRepositoryImpl
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthenticationRepositoryImplTest {
    private lateinit var localAuthenticationDataSource: LocalAuthenticationDataSource
    private lateinit var authenticationRepository: AuthenticationRepositoryImpl

    private val users = listOf(
        User("testId", "testUsername", "testPassword", UserRole.USER),
        User("testId2", "testUsername2", "testPassword2", UserRole.USER)
    )
    private val testUsername = "testUsername"
    private val testPassword = "testPassword"

    @BeforeEach
    fun setUp() {
        localAuthenticationDataSource = mockk(relaxed = true)
        authenticationRepository = AuthenticationRepositoryImpl(localAuthenticationDataSource)
    }

    @Test
    fun `getCurrentUser should return logged in user when user is logged in`() {
        every { authenticationRepository.getAllUsers() } returns users
        authenticationRepository.login(testUsername, testPassword)

        val result = authenticationRepository.getCurrentUser()

        assertThat(result).isEqualTo(users.first())
    }

    @Test
    fun `getCurrentUser should return null when user is not logged in`() {
        every { authenticationRepository.getAllUsers() } returns users

        val result = authenticationRepository.getCurrentUser()

        assertThat(result).isNull()
    }

    @Test
    fun `login should set and return the current user when user is logged in`() {
        every { localAuthenticationDataSource.getAllUsers() } returns users

        val loggedInUser = authenticationRepository.login(testUsername, testPassword)
        val currentUser = authenticationRepository.getCurrentUser()

        assertThat(loggedInUser).isEqualTo(currentUser)
        assertThat(loggedInUser.username).isEqualTo(testUsername)
        assertThat(loggedInUser.password).isEqualTo(testPassword)
        assertThat(loggedInUser.role).isEqualTo(UserRole.USER)
    }


    @Test
    fun `createMate should save and return the created user when create new mate`() {

        every { localAuthenticationDataSource.saveUser(any()) } returns Unit

        val createdUser = authenticationRepository.createMate(testUsername, testPassword)

        verify { localAuthenticationDataSource.saveUser(any()) }
        assertThat(createdUser.username).isEqualTo(testUsername)
        assertThat(createdUser.password).isEqualTo(testPassword)
        assertThat(createdUser.role).isEqualTo(UserRole.USER)
    }

    @Test
    fun `getAllUsers should call getAllUsers function from localAuthenticationDataSource when get all users`() {
        every { localAuthenticationDataSource.getAllUsers() } returns users

        val result = authenticationRepository.getAllUsers()

        assertThat(result).isEqualTo(users)
    }

    @Test
    fun `getUserId should throw exception when user is not found`() {
        every { localAuthenticationDataSource.getAllUsers() } returns users

        assertThrows<NoSuchElementException> {
            authenticationRepository.login("wrongUser", "wrongPassword")
        }
    }

    @Test
    fun `getUserRole should throw exception when user is not found`() {
        every { localAuthenticationDataSource.getAllUsers() } returns users

        assertThrows<NoSuchElementException> {
            authenticationRepository.login("nonExistentUser", "wrongPassword")
        }
    }
}