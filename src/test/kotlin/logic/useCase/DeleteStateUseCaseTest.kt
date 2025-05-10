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
import org.example.logic.useCase.DeleteStateUseCase
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteStateUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var deleteStateUseCase: DeleteStateUseCase
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

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        currentUserUseCase = mockk()
        updateProjectUseCase = mockk()
        deleteStateUseCase = DeleteStateUseCase(projectRepository, updateProjectUseCase)
    }

    @Test
    fun `should return updated project with deleted state when state id is valid and user is admin`() = runTest {
        coEvery { projectRepository.getProjectById(any()) } returns dummyProject
        coEvery { updateProjectUseCase(any()) } returns dummyProject.copy(
            states = dummyProject.states - State(
                id = "2",
                title = "StateTest"
            )
        )

        val updatedProject = deleteStateUseCase(stateId, dummyProject.id)

        coVerify { projectRepository.getProjectById(any()) }
        assertThat(updatedProject.states).hasSize(2)
    }

    @Test
    fun `should throw BlankInputException when state id is blank`() = runTest {
        val blankStateName = ""
        coEvery { currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            deleteStateUseCase(blankStateName, dummyProject.id)
        }
    }

    @Test
    fun `should throw BlankInputException when project id is blank`() = runTest {
        val blankProjectId = ""
        coEvery { currentUserUseCase() } returns createUser()

        assertThrows<BlankInputException> {
            deleteStateUseCase(stateId, blankProjectId)
        }
    }


    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() = runTest {
        coEvery { currentUserUseCase() } returns createUser()
        coEvery { projectRepository.getProjectById(any()) } returns null

        assertThrows<ProjectNotFoundException> {
            deleteStateUseCase(stateId, dummyProject.id)
        }
    }
}
