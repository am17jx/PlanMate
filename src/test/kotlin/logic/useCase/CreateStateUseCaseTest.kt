package logic.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import mockdata.createUser
import org.example.logic.models.State
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.CreateStateUseCase
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateStateUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var createStateUseCase: CreateStateUseCase
    private lateinit var projectStateRepository: ProjectStateRepository
    private val dummyProject =
        createProject(
            id = "1",
            states =
                listOf(
                    State(id = "2", title = "StateTest"),
                    State(id = "3", title = "StateTest2"),
                    State(id = "4", title = "StateTest3"),
                ),
            auditLogsIds = listOf("5", "6", "7"),
        )
    private val stateName = "StateTest4"

    @BeforeEach
    fun setUp() {
        projectStateRepository = mockk(relaxed = true)
        projectRepository = mockk(relaxed = true)
        updateProjectUseCase = mockk(relaxed = true)
        authenticationRepository = mockk(relaxed = true)
        createStateUseCase =
            CreateStateUseCase(projectStateRepository, projectRepository)
    }

    @Test
    fun `should return the updated project with the added state when given valid project id, state name is not blank and user is admin`() =
        runTest {
            coEvery { authenticationRepository.getCurrentUser() } returns createUser()
            coEvery { projectRepository.getProjectById(any()) } returns dummyProject
            coEvery { updateProjectUseCase(any()) } returns
                    dummyProject.copy(projectStateIds = dummyProject.projectStateIds + "8")

            val updatedProject = createStateUseCase(stateName, dummyProject.id)

            coVerify { projectRepository.getProjectById(any()) }

        }

    @Test
    fun `should throw BlankInputException when state name is blank`() = runTest {
        val blankStateName = ""
        coEvery { authenticationRepository.getCurrentUser() } returns createUser()

        assertThrows<BlankInputException> {
            createStateUseCase(blankStateName, dummyProject.id)
        }
    }

    @Test
    fun `should throw BlankInputException when project id is blank`() = runTest {
        val blankProjectId = ""
        coEvery { authenticationRepository.getCurrentUser() } returns createUser()

        assertThrows<BlankInputException> {
            createStateUseCase(stateName, blankProjectId)
        }
    }


    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() = runTest {
        coEvery { authenticationRepository.getCurrentUser() } returns createUser()
        coEvery { projectRepository.getProjectById(any()) } returns null

        assertThrows<ProjectNotFoundException> {
            createStateUseCase(stateName, dummyProject.id)
        }
    }
}
