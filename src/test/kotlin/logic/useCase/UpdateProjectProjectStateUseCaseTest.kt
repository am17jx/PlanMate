package logic.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import mockdata.createUser
import org.example.logic.models.ProjectState
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.UpdateProjectStateUseCase
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskStateNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateProjectProjectStateUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var updateProjectStateUseCase: UpdateProjectStateUseCase
    private lateinit var currentUserUseCase: GetCurrentUserUseCase
    private lateinit var projectStateRepository: ProjectStateRepository
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
    private val newTitle = "New State Title"

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        projectStateRepository = mockk()
        currentUserUseCase = mockk(relaxed = true)
        updateProjectUseCase = mockk()
        updateProjectStateUseCase = UpdateProjectStateUseCase(projectStateRepository,projectRepository)
    }

    @Test
    fun `should return updated project with deleted state when state id is valid and user is admin`() = runTest {
        val updatedStates = listOf("2","3","4")
        coEvery { currentUserUseCase() } returns createUser()
        coEvery { projectRepository.getProjectById(any()) } returns dummyProject
        coEvery { updateProjectUseCase(any()) } returns dummyProject.copy(projectStateIds = updatedStates)

        val updatedProject = updateProjectStateUseCase(newTitle, stateId, dummyProject.id)

        coVerify { projectRepository.getProjectById(any()) }
    }

    @Test
    fun `should throw BlankInputException when new state name is blank`() = runTest {
        val blankStateName = ""
        coEvery { currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            updateProjectStateUseCase(blankStateName, stateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw BlankInputException when state id is blank`() = runTest {
        val blankStateId = ""
        coEvery { currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            updateProjectStateUseCase(newTitle, blankStateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw BlankInputException when project id is blank`() = runTest {
        val blankProjectId = ""
        coEvery { currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            updateProjectStateUseCase(newTitle, stateId, blankProjectId)
        }
    }


    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() = runTest {
        coEvery { currentUserUseCase() } returns createUser()
        coEvery { projectRepository.getProjectById(any()) } returns null

        assertThrows<ProjectNotFoundException> {
            updateProjectStateUseCase(newTitle, stateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw StateNotFoundException when no state found with the given id`() = runTest {
        coEvery { currentUserUseCase() } returns createUser()
        coEvery { projectRepository.getProjectById(any()) } returns createProject()

        assertThrows<TaskStateNotFoundException> {
            updateProjectStateUseCase(newTitle, "5", dummyProject.id)
        }
    }
}
