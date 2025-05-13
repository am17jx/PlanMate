package logic.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import org.example.logic.models.AuditLog
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.DeleteProjectStateUseCase
import org.example.logic.useCase.GetProjectStatesUseCase
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.utils.Constants
import org.example.logic.utils.ProjectNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DeleteProjectStateUseCaseTest {
    private lateinit var projectStateRepository: ProjectStateRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var getProjectTasksUseCase: GetProjectTasksUseCase
    private lateinit var getProjectStatesUseCase: GetProjectStatesUseCase
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var deleteProjectStateUseCase: DeleteProjectStateUseCase
    private val id1 = Uuid.random()
    private val id2 = Uuid.random()
    private val dummyProject =
        createProject(
            id = id1,
        )
    private val stateId = id2

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        projectStateRepository = mockk(relaxed = true)
        getProjectTasksUseCase = mockk(relaxed = true)
        getProjectStatesUseCase = mockk(relaxed = true)
        createAuditLogUseCase = mockk(relaxed = true)
        deleteProjectStateUseCase =
            DeleteProjectStateUseCase(
                projectStateRepository,
                taskRepository,
                getProjectTasksUseCase,
                getProjectStatesUseCase,
                createAuditLogUseCase,
            )
    }


    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() =
        runTest {
            coEvery { getProjectStatesUseCase(any()) } throws ProjectNotFoundException()

            assertThrows<ProjectNotFoundException> {
                deleteProjectStateUseCase(stateId, dummyProject.id)
            }
        }


    @Test
    fun `should log update and delete state even if no tasks exist for state`() = runTest {
        val states = listOf(
            mockk<org.example.logic.models.ProjectState> { coEvery { id } returns stateId; coEvery { title } returns "State 1" },
            mockk<org.example.logic.models.ProjectState> { coEvery { id } returns Uuid.random(); coEvery { title } returns "State 2" }
        )

        coEvery { getProjectTasksUseCase(dummyProject.id) } returns emptyList()
        coEvery { getProjectStatesUseCase(dummyProject.id) } returns states

        deleteProjectStateUseCase(stateId, dummyProject.id)

        coVerify(exactly = 0) { taskRepository.deleteTask(any()) }

        coVerify { createAuditLogUseCase.logUpdate(
            entityType = AuditLog.EntityType.PROJECT,
            entityId = dummyProject.id,
            entityName = "",
            fieldChange = AuditLog.FieldChange(
                fieldName = Constants.FIELD_STATES,
                oldValue = "State 1, State 2",
                newValue = "State 1"
            )
        ) }

        coVerify { projectStateRepository.deleteProjectState(stateId) }
    }

    @Test
    fun `should delete tasks matching the given stateId`() = runTest {
        val task1Id = Uuid.random()
        val task2Id = Uuid.random()
        val task3Id = Uuid.random()

        val task1 = mockk<org.example.logic.models.Task>(relaxed = true).apply {
            every { id } returns task1Id
            every { stateId } returns stateId
        }

        val task2 = mockk<org.example.logic.models.Task>(relaxed = true).apply {
            every { id } returns task2Id
            every { stateId } returns stateId
        }

        val task3 = mockk<org.example.logic.models.Task>(relaxed = true).apply {
            every { id } returns task3Id
            every { stateId } returns Uuid.random()
        }

        coEvery { getProjectTasksUseCase(dummyProject.id) } returns listOf(task1, task2, task3)

        deleteProjectStateUseCase(stateId, dummyProject.id)

        coVerify(exactly = 1) { taskRepository.deleteTask(task1Id) }
        coVerify(exactly = 1) { taskRepository.deleteTask(task2Id) }
        coVerify(exactly = 0) { taskRepository.deleteTask(task3Id) }
    }
}
