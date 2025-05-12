package org.example.presentation.screens

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.useCase.CreateUserUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidUsernameException
import org.example.logic.utils.UserAlreadyExistsException
import org.junit.Test
import presentation.utils.io.Reader
import presentation.utils.io.Viewer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateUserUiTest {
    private val createUserUseCase: CreateUserUseCase = mockk()
    private val readerMock: Reader = mockk()
    private val viewerMock: Viewer = mockk()
    private val onBackMock: () -> Unit = mockk(relaxed = true)

    @Test
    fun `should return success message when user is created successfully`() {
        val username = "newUser"
        val password = "password123"
        val user =
            User(
                id = Uuid.random(),
                username = username,
                role = UserRole.USER,
                authMethod = User.AuthenticationMethod.Password(password),
            )
        every { readerMock.readString() } returns username andThen password
        coEvery { createUserUseCase(username, password) } returns user

        val createUserUi = CreateUserUi(createUserUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("✅ User '${user.username}' created successfully with role: ${user.role}") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when username is blank`() {
        val exceptionMessage = "Username cannot be blank"
        every { readerMock.readString() } returns "" andThen "password123"
        coEvery { createUserUseCase(any(), any()) } throws BlankInputException()

        val createUserUi = CreateUserUi(createUserUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when password is blank`() {
        val exceptionMessage = "Password cannot be blank"
        every { readerMock.readString() } returns "newUser" andThen ""
        coEvery { createUserUseCase(any(), any()) } throws BlankInputException()

        val createUserUi = CreateUserUi(createUserUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when username is invalid`() {
        val exceptionMessage = "Invalid username input"
        every { readerMock.readString() } returns "invalid#user" andThen "password123"
        coEvery { createUserUseCase(any(), any()) } throws InvalidUsernameException()

        val createUserUi = CreateUserUi(createUserUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when user already exists`() {
        val exceptionMessage = "User already exists"
        every { readerMock.readString() } returns "existingUser" andThen "password123"
        coEvery { createUserUseCase(any(), any()) } throws UserAlreadyExistsException()

        val createUserUi = CreateUserUi(createUserUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when an unexpected exception occurs`() {
        val exceptionMessage = "Unexpected error"
        every { readerMock.readString() } returns "newUser" andThen "password123"
        coEvery { createUserUseCase(any(), any()) } throws Exception(exceptionMessage)

        val createUserUi = CreateUserUi(createUserUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Unexpected error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }
}
