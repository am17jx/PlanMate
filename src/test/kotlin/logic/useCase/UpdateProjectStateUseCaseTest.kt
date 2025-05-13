package logic.useCase

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.logic.models.ProjectState
import org.example.logic.models.Task
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.*
import org.example.logic.utils.BlankInputException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateProjectStateUseCaseTest {
    private lateinit var projectStateRepository: ProjectStateRepository
    private lateinit var getProjectStatesUseCase: GetProjectStatesUseCase
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var getTasksByProjectStateUseCase: GetTasksByProjectStateUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase
    private lateinit var validation: Validation
    private lateinit var updateProjectStateUseCase: UpdateProjectStateUseCase

    private val projectId = Uuid.random()
    private val stateId = Uuid.random()
    private val newStateName = "New State Name"
    private val oldStateName = "Old State Name"
    private val oldProjectStates = listOf(
        ProjectState(Uuid.random(), "State 1", projectId),
        ProjectState(stateId, oldStateName, projectId),
        ProjectState(Uuid.random(), "State 3", projectId)
    )
    private val tasks = listOf(
        Task(Uuid.random(), "Task 1", stateId, oldStateName, projectId, "User 1", projectId),
        Task(Uuid.random(), "Task 2", stateId, oldStateName, projectId, "User 2", projectId)
    )

    @BeforeEach
    fun setUp() {
        projectStateRepository = mockk(relaxed = true)
        getProjectStatesUseCase = mockk(relaxed = true)
        createAuditLogUseCase = mockk(relaxed = true)
        getTasksByProjectStateUseCase = mockk(relaxed = true)
        updateTaskUseCase = mockk(relaxed = true)
        validation = mockk(relaxed = true)
        updateProjectStateUseCase = UpdateProjectStateUseCase(
            projectStateRepository,
            getProjectStatesUseCase,
            createAuditLogUseCase,
            getTasksByProjectStateUseCase,
            updateTaskUseCase,
            validation
        )
    }

    @Test
    fun `should update project state and associated tasks when update is successful`() = runTest {

        every { validation.validateInputNotBlankOrThrow(newStateName) } just Runs
        coEvery { getProjectStatesUseCase(projectId) } returns oldProjectStates
        coEvery { getTasksByProjectStateUseCase(stateId) } returns tasks

        updateProjectStateUseCase(newStateName, stateId, projectId)

        coVerify { validation.validateInputNotBlankOrThrow(newStateName) }
        coVerify { getProjectStatesUseCase(projectId) }
        tasks.forEach { task ->
            coVerify {
                updateTaskUseCase(task.copy(stateName = newStateName))
            }
        }
    }

    @Test
    fun `should throw exception when newStateName is blank`() = runTest {

        every { validation.validateInputNotBlankOrThrow("") } throws BlankInputException()

        assertThrows<BlankInputException> {
            updateProjectStateUseCase("", stateId, projectId)
        }
    }
}
