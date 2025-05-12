package logic.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.example.logic.models.ProjectState
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.CreateProjectStateUseCase
import org.example.logic.useCase.GetProjectStatesUseCase
import org.example.logic.useCase.Validation
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    private val dummyProjectStates =
        listOf(
            ProjectState(
                id = id1,
                title = "StateTest1",
                projectId = id1,
            ),
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
    fun `should return the updated project with the added state when given valid project id, state name is not blank and project exists`() =
        runTest {
            coEvery { getProjectStatesUseCase(any()) } returns dummyProjectStates

            createProjectStateUseCase(Uuid.random(), stateName)

            verify { validation.validateInputNotBlankOrThrow(any()) }
            coVerify { projectStateRepository.createProjectState(any()) }
        }

    @Test
    fun `should throw BlankInputException when state name is blank`() =
        runTest {
            val blankStateName = ""
            every { validation.validateInputNotBlankOrThrow(blankStateName) } throws BlankInputException()

            assertThrows<BlankInputException> {
                createProjectStateUseCase(Uuid.random(), blankStateName)
            }
        }

    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() =
        runTest {
            coEvery { getProjectStatesUseCase(any()) } throws ProjectNotFoundException()

            assertThrows<ProjectNotFoundException> {
                createProjectStateUseCase(Uuid.random(), stateName)
            }
        }
}
