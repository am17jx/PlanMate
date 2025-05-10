package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import mockdata.createUser
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.UpdateStateUseCase
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.StateNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateStateUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var updateStateUseCase: UpdateStateUseCase
    private lateinit var currentUserUseCase: GetCurrentUserUseCase
    private val dummyProject = createProject(
        id = "1",
        states = listOf(
            State(id = "2", title = "StateTest"),
            State(id = "3", title = "StateTest2"),
            State(id = "4", title = "StateTest3"),
        ),
        auditLogsIds = listOf("5", "6", "7"),
    )
    private val stateId = "2"
    private val newTitle = "New State Title"

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        currentUserUseCase = mockk(relaxed = true)
        updateProjectUseCase = mockk()
        updateStateUseCase = UpdateStateUseCase(projectRepository,updateProjectUseCase)
    }

    @Test
    fun `should return updated project with deleted state when state id is valid and user is admin`() = runTest {
        val updatedStates = listOf(
            State(id = "2", title = newTitle),
            State(id = "3", title = "StateTest2"),
            State(id = "4", title = "StateTest3"),
        )
        coEvery {currentUserUseCase() } returns createUser()
        coEvery { projectRepository.getProjectById(any()) } returns dummyProject
        coEvery { updateProjectUseCase(any()) } returns dummyProject.copy(tasksStatesIds = updatedStates)

        val updatedProject = updateStateUseCase(newTitle, stateId, dummyProject.id)

        coVerify { projectRepository.getProjectById(any()) }
        assertThat(updatedProject.tasksStatesIds).hasSize(3)
        assertThat(updatedProject.tasksStatesIds.find { it.id == stateId }?.title).isEqualTo(newTitle)
    }

    @Test
    fun `should throw BlankInputException when new state name is blank`() = runTest {
        val blankStateName = ""
        coEvery {currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            updateStateUseCase(blankStateName, stateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw BlankInputException when state id is blank`() = runTest {
        val blankStateId = ""
        coEvery {currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            updateStateUseCase(newTitle, blankStateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw BlankInputException when project id is blank`() = runTest {
        val blankProjectId = ""
        coEvery {currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            updateStateUseCase(newTitle, stateId, blankProjectId)
        }
    }


    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() = runTest {
        coEvery {currentUserUseCase() } returns createUser()
        coEvery { projectRepository.getProjectById(any()) } returns null

        assertThrows<ProjectNotFoundException> {
            updateStateUseCase(newTitle, stateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw StateNotFoundException when no state found with the given id`() = runTest {
        coEvery {currentUserUseCase() } returns createUser()
        coEvery { projectRepository.getProjectById(any()) } returns createProject()

        assertThrows<StateNotFoundException> {
            updateStateUseCase(newTitle, "5", dummyProject.id)
        }
    }
}
