package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mockdata.createProject
import mockdata.createState
import mockdata.createTask
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.creatTask.CreateTaskUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.StateNotFoundException
import org.example.logic.utils.UserNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateTaskUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var projectRepository: ProjectRepository
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var createTaskUseCase: CreateTaskUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        projectRepository = mockk(relaxed = true)
        authenticationRepository = mockk(relaxed = true)
        auditLogRepository = mockk(relaxed = true)
        createTaskUseCase =
            CreateTaskUseCase(taskRepository, projectRepository, authenticationRepository, auditLogRepository)
    }

    @Test
    fun `should return created task when there is no blank input parameters and project and state exist`() {
        val taskName = "Write CreateTaskUseCase test cases"
        val projectId = Uuid.random().getCroppedId()
        val stateId = Uuid.random().getCroppedId()
        every { taskRepository.createTask(any()) } returns
            createTask(
                name = taskName,
                projectId = projectId,
                stateId = stateId,
                auditLogsIds = listOf("task-id"),
            )
        every { projectRepository.getProjectById(any()) } returns
            createProject(
                id = projectId,
                states =
                    listOf(
                        createState(id = stateId),
                    ),
            )

        val result = createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)

        verify { projectRepository.getProjectById(projectId) }
        verify { authenticationRepository.getCurrentUser() }
        verify { taskRepository.createTask(any()) }
        verify { auditLogRepository.createAuditLog(any()) }
        assertThat(result.name).isEqualTo(taskName)
        assertThat(result.projectId).isEqualTo(projectId)
        assertThat(result.stateId).isEqualTo(stateId)
        assertThat(result.auditLogsIds).isNotEmpty()
    }

    @ParameterizedTest
    @MethodSource("provideBlankInputScenarios")
    fun `should throw BlankInputException when any of the inputs is blank`(
        taskName: String,
        projectId: String,
        stateId: String,
    ) {
        assertThrows<BlankInputException> {
            createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)
        }
    }

    @Test
    fun `should throw ProjectNotFoundException when project doesn't exist`() {
        val taskName = "Test"
        val projectId = Uuid.random().getCroppedId()
        val stateId = Uuid.random().getCroppedId()
        every { projectRepository.getProjectById(any()) } returns null

        assertThrows<ProjectNotFoundException> {
            createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)
        }
    }

    @Test
    fun `should throw StateNotFoundException when state doesn't exist`() {
        val taskName = "Test"
        val projectId = Uuid.random().getCroppedId()
        val stateId = "1"
        val differentStateId = "3"
        every { projectRepository.getProjectById(any()) } returns createProject(states = listOf(createState(id = differentStateId)))

        assertThrows<StateNotFoundException> {
            createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)
        }
    }

    @Test
    fun `should throw UserNotFoundException when no user is logged in`() {
        val taskName = "Test"
        val projectId = Uuid.random().getCroppedId()
        val stateId = Uuid.random().getCroppedId()
        every { projectRepository.getProjectById(any()) } returns createProject(states = listOf(createState(id = stateId)))
        every { authenticationRepository.getCurrentUser() } returns null

        assertThrows<UserNotFoundException> {
            createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)
        }
    }

    companion object {
        @JvmStatic
        fun provideBlankInputScenarios() =
            Stream.of(
                Arguments.argumentSet(
                    "blank task name",
                    "",
                    Uuid.random().getCroppedId(),
                    Uuid.random().getCroppedId(),
                ),
                Arguments.argumentSet(
                    "blank project id",
                    "test name",
                    "",
                    Uuid.random().getCroppedId(),
                ),
                Arguments.argumentSet(
                    "blank state id",
                    "test name",
                    Uuid.random().getCroppedId(),
                    "",
                ),
            )
    }
}
