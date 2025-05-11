package logic.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import mockdata.createUser
import org.example.logic.models.ProjectState
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.DeleteProjectStateUseCase
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteProjectStateUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var deleteProjectStateUseCase: DeleteProjectStateUseCase
    private lateinit var currentUserUseCase: GetCurrentUserUseCase
    private lateinit var projectStateRepository: ProjectStateRepository
    private lateinit var taskRepository: TaskRepository
    private val dummyProject = createProject(
        id = "1",
        projectStates = listOf(
            ProjectState(id = "2", title = "StateTest"),
            ProjectState(id = "3", title = "StateTest2"),
            ProjectState(id = "4", title = "StateTest3"),
        ),
        auditLogsIds = listOf("5", "6", "7"),
    )
    private val stateId = "2"

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        projectStateRepository = mockk()
        projectRepository = mockk()
        currentUserUseCase = mockk()
        updateProjectUseCase = mockk()
        deleteProjectStateUseCase = DeleteProjectStateUseCase(projectStateRepository, projectRepository, taskRepository)

    }

    @Test
    fun `should return updated project with deleted state when state id is valid and user is admin`() = runTest {
        coEvery { projectRepository.getProjectById(any()) } returns dummyProject
        coEvery { updateProjectUseCase(any()) } returns dummyProject.copy(
            projectStateIds = dummyProject.projectStateIds - "2"
        )

        deleteProjectStateUseCase(stateId, dummyProject.id)

        coVerify { projectRepository.getProjectById(any()) }
    }

    @Test
    fun `should throw BlankInputException when state id is blank`() = runTest {
        val blankStateName = ""
        coEvery { currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            deleteProjectStateUseCase(blankStateName, dummyProject.id)
        }
    }

    @Test
    fun `should throw BlankInputException when project id is blank`() = runTest {
        val blankProjectId = ""
        coEvery { currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            deleteProjectStateUseCase(stateId, blankProjectId)
        }
    }


    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() = runTest {
        coEvery { currentUserUseCase() } returns createUser()
        coEvery { projectRepository.getProjectById(any()) } returns null

        assertThrows<ProjectNotFoundException> {
            deleteProjectStateUseCase(stateId, dummyProject.id)
        }
    }
}
