package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import mockdata.createState
import mockdata.createTask
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.GetStateNameUseCase
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.utils.TaskNotFoundException
import org.example.logic.utils.TaskStateNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetProjectStateNameUseCaseTest {
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var getStateNameUseCase: GetStateNameUseCase
    private lateinit var projectStateRepository: ProjectStateRepository
    private val ids = List(3) { Uuid.random() }
    private val taskId = Uuid.random()
    private val stateId = Uuid.random()
    private val dummyTask = createTask(
        id = ids[1],
        name = "to do",
        stateId = ids[2]
    )
    private val dummyState = createState(
        id = ids[2],
        title = "to do"
    )
    private val projectId = Uuid.random()

    @BeforeEach
    fun setUp() {
        projectStateRepository = mockk(relaxed = true)
        getTaskByIdUseCase = mockk(relaxed = true)
        getStateNameUseCase = GetStateNameUseCase(getTaskByIdUseCase, projectStateRepository)
    }

    @Test
    fun `should return state name when task exists`() = runTest {
        val taskId = ids[1]
        val expectedStateName = "to do"
        coEvery { getTaskByIdUseCase(taskId) } returns dummyTask
        coEvery { projectStateRepository.getProjectStateById(ids[2]) } returns dummyState

        val result = getStateNameUseCase(taskId)

        assertThat(result).isEqualTo(expectedStateName)
    }

    @Test
    fun `should throw TaskNotFoundException when task doesn't exist`() = runTest {
        val taskId = ids[2]
        coEvery { getTaskByIdUseCase(taskId) } throws TaskNotFoundException()

        assertThrows<TaskNotFoundException> {
            getStateNameUseCase(taskId)
        }
    }

    @Test
    fun `should throw TaskStateNotFoundException when state doesn't exist`() = runTest {
        val taskId = ids[1]
        val stateId = ids[2]
        coEvery { getTaskByIdUseCase(taskId) } returns dummyTask
        coEvery { projectStateRepository.getProjectStateById(stateId) } returns null

        assertThrows<TaskStateNotFoundException> {
            getStateNameUseCase(taskId)
        }
    }

}