package logic.useCase

import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.CreateProjectStateUseCase
import org.example.logic.useCase.GetProjectStatesUseCase
import org.example.logic.useCase.Validation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateProjectStateUseCaseTest {
    private lateinit var projectStateRepository: ProjectStateRepository
    private lateinit var getProjectStatesUseCase: GetProjectStatesUseCase
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var validation: Validation
    private lateinit var createProjectStateUseCase: CreateProjectStateUseCase
    private val id1 = Uuid.random()

    private val dummyProject =
        createProject(
            id = id1,
        )
    private val stateName = "StateTest4"

    @BeforeEach
    fun setUp() {
        projectStateRepository = mockk(relaxed = true)
        getProjectStatesUseCase = mockk(relaxed = true)
        createAuditLogUseCase = mockk(relaxed = true)
        validation = mockk(relaxed = true)
        createProjectStateUseCase =
            CreateProjectStateUseCase(
                projectStateRepository,
                getProjectStatesUseCase,
                createAuditLogUseCase,
                validation,
            )
    }

    @Test
    fun `should return the updated project with the added state when given valid project id, state name is not blank and user is admin`() =
        runTest {
//            coEvery { authenticationRepository.getCurrentUser() } returns createUser()
//            coEvery { projectRepository.getProjectById(any()) } returns dummyProject
//
//            val updatedProject = createProjectStateUseCase(id1, stateName)
//
//            coVerify { projectRepository.getProjectById(any()) }
            assertTrue(false)
        }

    @Test
    fun `should throw BlankInputException when state name is blank`() =
        runTest {
//            val blankStateName = ""
//            coEvery { authenticationRepository.getCurrentUser() } returns createUser()
//
//            assertThrows<BlankInputException> {
//                createProjectStateUseCase(Uuid.random(), blankStateName)
//            }
            assertTrue { false }
        }

    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() =
        runTest {
//            coEvery { authenticationRepository.getCurrentUser() } returns createUser()
//            coEvery { projectRepository.getProjectById(any()) } returns null
//
//            assertThrows<ProjectNotFoundException> {
//                createProjectStateUseCase(Uuid.random(), stateName)
//            }
            assertTrue(false)
        }
}
