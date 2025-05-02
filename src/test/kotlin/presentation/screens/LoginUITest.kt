package org.example.presentation.screens

import io.mockk.*
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.useCase.LoginUserUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.UserNotFoundException
import presentation.utils.io.Reader
import presentation.utils.io.Viewer
import org.junit.Test

class LoginUITest {

    private val loginUserUseCase: LoginUserUseCase = mockk()
    private val readerMock: Reader = mockk()
    private val viewerMock: Viewer = mockk()
    private val onNavigateToAdminHomeMock: () -> Unit = mockk(relaxed = true)
    private val onNavigateToShowAllProjectsMock: (userRole: UserRole) -> Unit = mockk(relaxed = true)

    @Test
    fun `should navigate to Admin Home when login is successful and role is ADMIN`() {
        val username = "adminUser"
        val password = "adminPassword"

        val user = User(id = "1", username = username, password = password, role = UserRole.ADMIN)

        every { readerMock.readString() } returns username andThen password
        every { loginUserUseCase(username, password) } returns user

        val loginUI = LoginUI(onNavigateToAdminHomeMock, onNavigateToShowAllProjectsMock, loginUserUseCase, readerMock, viewerMock)

        verify { onNavigateToAdminHomeMock() }
        verify { viewerMock.display("====================================") }
    }

    @Test
    fun `should navigate to Show All Projects when login is successful and role is USER`() {
        val username = "user"
        val password = "userPassword"

        // Create a mock User object directly with the role set to USER
        val user = User(id = "2", username = username, password = password, role = UserRole.USER)

        every { readerMock.readString() } returns username andThen password
        every { loginUserUseCase(username, password) } returns user

        val loginUI = LoginUI(onNavigateToAdminHomeMock, onNavigateToShowAllProjectsMock, loginUserUseCase, readerMock, viewerMock)

        verify { onNavigateToShowAllProjectsMock(UserRole.USER) }
        verify { viewerMock.display("====================================") }
    }

    @Test
    fun `should display error message when username or password is blank`() {
        val exceptionMessage = "Username or password cannot be blank"
        every { readerMock.readString() } returns "" andThen "password123"
        every { loginUserUseCase(any(), any()) } throws BlankInputException(exceptionMessage)

        val loginUI = LoginUI(onNavigateToAdminHomeMock, onNavigateToShowAllProjectsMock, loginUserUseCase, readerMock, viewerMock)

        verify { viewerMock.display("Error: $exceptionMessage") }
    }

    @Test
    fun `should display error message when user is not found`() {
        val exceptionMessage = "User not found"
        every { readerMock.readString() } returns "nonExistentUser" andThen "password123"
        every { loginUserUseCase(any(), any()) } throws UserNotFoundException(exceptionMessage)

        val loginUI = LoginUI(onNavigateToAdminHomeMock, onNavigateToShowAllProjectsMock, loginUserUseCase, readerMock, viewerMock)

        verify { viewerMock.display("Error: $exceptionMessage") }
    }

    @Test
    fun `should display unexpected error message for any other exception`() {
        val exceptionMessage = "Unexpected error"
        every { readerMock.readString() } returns "user" andThen "password123"
        every { loginUserUseCase(any(), any()) } throws Exception(exceptionMessage)

        val loginUI = LoginUI(onNavigateToAdminHomeMock, onNavigateToShowAllProjectsMock, loginUserUseCase, readerMock, viewerMock)

        verify { viewerMock.display("Unexpected error: $exceptionMessage") }
    }
}
