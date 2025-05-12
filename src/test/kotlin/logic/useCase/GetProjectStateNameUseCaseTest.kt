package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import mockdata.createTask
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.GetStateNameUseCase
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.utils.TaskStateNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetProjectStateNameUseCaseTest {
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var getStateNameUseCase: GetStateNameUseCase
    private lateinit var projectStateRepository: ProjectStateRepository
    private val ids = List(6) { Uuid.random() }
    private val dummyTask = createTask(
        id = ids[1],
        name = "Some Task",
    )

    private val dummyProject = createProject(
        id = ids[3],
        name = "My Project"
    )

    @BeforeEach
    fun setUp() {
        projectStateRepository = mockk()
        getTaskByIdUseCase = mockk()
        getProjectByIdUseCase = mockk()
        getStateNameUseCase = GetStateNameUseCase(getTaskByIdUseCase, projectStateRepository)
    }

    @Test
    fun `should return sate name when take and project are available`() = runTest {
        val taskId = ids[1]
        val projectId = ids[3]
        val expectedStateName = "to do"
        coEvery { getProjectByIdUseCase(projectId) } returns dummyProject
        coEvery { getTaskByIdUseCase(taskId) } returns dummyTask


        val result = getStateNameUseCase(taskId)

        assertThat(result).isEqualTo(expectedStateName)
    }

    @Test
    fun `should return null when there is no state`() = runTest {
        val taskId = ids[2]
        val projectId = ids[4]
        val missingStateTask = dummyTask.copy(stateId = ids[5])
        coEvery { getProjectByIdUseCase(projectId) } returns dummyProject
        coEvery { getTaskByIdUseCase(taskId) } returns missingStateTask

        assertThrows<TaskStateNotFoundException> {
            getStateNameUseCase(taskId)
        }
    }
}