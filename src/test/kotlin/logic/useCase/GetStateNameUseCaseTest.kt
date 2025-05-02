package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.models.Task
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.GetStateNameUseCase
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.utils.StateNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetStateNameUseCaseTest {
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var getStateNameUseCase: GetStateNameUseCase
    private val dummyTask = Task(
        id = "task-123",
        name = "Some Task",
        stateId = "state-001",
        addedBy = "user-1",
        auditLogsIds = emptyList(),
        projectId = "project-1"
    )

    private val dummyProject = Project(
        id = "project-1",
        name = "My Project",
        states = listOf(
            State(id = "state-001", title = "to do"),
            State(id = "state-002", title = "in progress")
        ),
        auditLogsIds = emptyList()
    )
    @BeforeEach
    fun setUp() {
        getTaskByIdUseCase = mockk()
        getProjectByIdUseCase = mockk()
        getStateNameUseCase = GetStateNameUseCase(getTaskByIdUseCase, getProjectByIdUseCase)
    }
    @Test
    fun `should return sate name when take and project are available`() {
        val taskId = "task-123"
        val projectId = "project-1"
        val expectedStateName = "to do"
        every { getProjectByIdUseCase(projectId) } returns dummyProject
        every { getTaskByIdUseCase(taskId) } returns dummyTask


        val result = getStateNameUseCase(taskId)

        assertThat(result).isEqualTo(expectedStateName)
    }
    @Test
    fun `should return null when there is no state`() {
        val taskId = "task-123"
        val projectId = "project-1"
        val missingStateTask = dummyTask.copy(stateId = "state-999")
        every { getProjectByIdUseCase(projectId) } returns dummyProject
        every { getTaskByIdUseCase(taskId) } returns missingStateTask

        assertThrows<StateNotFoundException>{
            getStateNameUseCase(taskId)
        }
    }
}