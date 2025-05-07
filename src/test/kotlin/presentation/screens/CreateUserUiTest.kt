package org.example.presentation.screens

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.useCase.CreateMateUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidUserNameInputException
import org.example.logic.utils.UserAlreadyExistsException
import org.junit.Test
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class CreateUserUiTest {

    private val createMateUseCase: CreateMateUseCase = mockk()
    private val readerMock: Reader = mockk()
    private val viewerMock: Viewer = mockk()
    private val onBackMock: () -> Unit = mockk(relaxed = true)

    @Test
    fun `should return success message when user is created successfully`() {
        val username = "newUser"
        val password = "password123"
        val user = User(id = "123", username = username, password = password, role = UserRole.USER)
        every { readerMock.readString() } returns username andThen password
        coEvery { createMateUseCase(username, password) } returns user

        val createUserUi = CreateUserUi(createMateUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("✅ User '${user.username}' created successfully with role: ${user.role}") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when username is blank`() {
        val exceptionMessage = "Username cannot be blank"
        every { readerMock.readString() } returns "" andThen "password123"
        coEvery { createMateUseCase(any(), any()) } throws BlankInputException(exceptionMessage)

        val createUserUi = CreateUserUi(createMateUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when password is blank`() {
        val exceptionMessage = "Password cannot be blank"
        every { readerMock.readString() } returns "newUser" andThen ""
        coEvery { createMateUseCase(any(), any()) } throws BlankInputException(exceptionMessage)

        val createUserUi = CreateUserUi(createMateUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when username is invalid`() {
        val exceptionMessage = "Invalid username input"
        every { readerMock.readString() } returns "invalid#user" andThen "password123"
        coEvery { createMateUseCase(any(), any()) } throws InvalidUserNameInputException(exceptionMessage)

        val createUserUi = CreateUserUi(createMateUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when user already exists`() {
        val exceptionMessage = "User already exists"
        every { readerMock.readString() } returns "existingUser" andThen "password123"
        coEvery { createMateUseCase(any(), any()) } throws UserAlreadyExistsException(exceptionMessage)

        val createUserUi = CreateUserUi(createMateUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when an unexpected exception occurs`() {
        val exceptionMessage = "Unexpected error"
        every { readerMock.readString() } returns "newUser" andThen "password123"
        coEvery { createMateUseCase(any(), any()) } throws Exception(exceptionMessage)

        val createUserUi = CreateUserUi(createMateUseCase, readerMock, viewerMock, onBackMock)

        verify { viewerMock.display("❌ Unexpected error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }
}
