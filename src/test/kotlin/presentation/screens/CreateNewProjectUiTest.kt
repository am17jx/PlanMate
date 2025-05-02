import io.mockk.*
import org.example.logic.models.Project
import org.example.presentation.screens.CreateNewProjectUi
import org.example.logic.useCase.CreateProjectUseCase
import org.example.logic.utils.*
import presentation.utils.io.Reader
import presentation.utils.io.Viewer
import org.junit.Test

class CreateNewProjectUiTest {

    private val createProjectUseCase: CreateProjectUseCase = mockk()
    private val onBackMock: () -> Unit = mockk(relaxed = true)
    private val readerMock: Reader = mockk()
    private val viewerMock: Viewer = mockk()

    @Test
    fun `should return success message when project is created successfully`() {
        val projectName = "New Project"
        val project = Project(
            id = "1",
            name = projectName,
            states = emptyList(),
            auditLogsIds = emptyList()
        )
        every { createProjectUseCase(projectName) } returns project

        val createNewProjectUi = CreateNewProjectUi(createProjectUseCase, onBackMock, readerMock, viewerMock)

        verify { viewerMock.display("✅ Project '${project.name}' created successfully with ID: ${project.id}") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when project name is blank`() {
        val exceptionMessage = "Project name cannot be blank"
        every { readerMock.readString() } returns ""
        every { createProjectUseCase(any()) } throws BlankInputException(exceptionMessage)

        val createNewProjectUi = CreateNewProjectUi(createProjectUseCase, onBackMock, readerMock, viewerMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when project creation fails`() {
        val exceptionMessage = "Project creation failed"
        every { readerMock.readString() } returns "Project Name"
        every { createProjectUseCase(any()) } throws ProjectCreationFailedException(exceptionMessage)

        val createNewProjectUi = CreateNewProjectUi(createProjectUseCase, onBackMock, readerMock, viewerMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when no user is logged in`() {
        val exceptionMessage = "No user logged in"
        every { readerMock.readString() } returns "Project Name"
        every { createProjectUseCase(any()) } throws NoLoggedInUserException(exceptionMessage)

        val createNewProjectUi = CreateNewProjectUi(createProjectUseCase, onBackMock, readerMock, viewerMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when the user is unauthorized to create a project`() {
        val exceptionMessage = "User is not authorized"
        every { readerMock.readString() } returns "Project Name"
        every { createProjectUseCase(any()) } throws UnauthorizedException(exceptionMessage)

        val createNewProjectUi = CreateNewProjectUi(createProjectUseCase, onBackMock, readerMock, viewerMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when there is an invalid audit input`() {
        val exceptionMessage = "Invalid audit input"
        every { readerMock.readString() } returns "Project Name"
        every { createProjectUseCase(any()) } throws AuditInputException(exceptionMessage)

        val createNewProjectUi = CreateNewProjectUi(createProjectUseCase, onBackMock, readerMock, viewerMock)

        verify { viewerMock.display("❌ Error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }

    @Test
    fun `should return error message when an unexpected exception occurs`() {
        val exceptionMessage = "Unexpected error"
        every { readerMock.readString() } returns "Project Name"
        every { createProjectUseCase(any()) } throws Exception(exceptionMessage)

        val createNewProjectUi = CreateNewProjectUi(createProjectUseCase, onBackMock, readerMock, viewerMock)

        verify { viewerMock.display("❌ Unexpected error: $exceptionMessage") }
        verify { onBackMock.invoke() }
    }
}
