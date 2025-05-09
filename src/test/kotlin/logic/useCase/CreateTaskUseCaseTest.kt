package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import mockdata.createState
import mockdata.createTask
import mockdata.createUser
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.StateNotFoundException
import org.example.logic.utils.UserNotFoundException
import org.example.logic.utils.getCroppedId
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
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var createTaskUseCase: CreateTaskUseCase
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        projectRepository = mockk(relaxed = true)
        auditLogRepository = mockk(relaxed = true)
        getCurrentUserUseCase = mockk(relaxed = true)
        createTaskUseCase =
            CreateTaskUseCase(taskRepository, projectRepository, auditLogRepository , getCurrentUserUseCase)
    }

    @Test
    fun `should return created task when there is no blank input parameters and project and state exist`() = runTest {
        val taskName = "Write CreateTaskUseCase test cases"
        val projectId = Uuid.random().getCroppedId()
        val stateId = Uuid.random().getCroppedId()
        coEvery { getCurrentUserUseCase() } returns createUser()
        coEvery { taskRepository.createTask(any()) } returns
            createTask(
                name = taskName,
                projectId = projectId,
                stateId = stateId,
                auditLogsIds = listOf("task-id"),
            )
        coEvery{ projectRepository.getProjectById(any()) } returns
            createProject(
                id = projectId,
                states =
                    listOf(
                        createState(id = stateId),
                    ),
            )

        val result = createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)

        coVerify { projectRepository.getProjectById(projectId) }
        coVerify{ getCurrentUserUseCase() }
        coVerify { taskRepository.createTask(any()) }
        coVerify{ auditLogRepository.createAuditLog(any()) }
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
    ) = runTest{
        assertThrows<BlankInputException> {
            createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)
        }
    }

    @Test
    fun `should throw ProjectNotFoundException when project doesn't exist`() = runTest {
        val taskName = "Test"
        val projectId = Uuid.random().getCroppedId()
        val stateId = Uuid.random().getCroppedId()
        coEvery { projectRepository.getProjectById(any()) } returns null

        assertThrows<ProjectNotFoundException> {
            createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)
        }
    }

    @Test
    fun `should throw StateNotFoundException when state doesn't exist`() = runTest {
        val taskName = "Test"
        val projectId = Uuid.random().getCroppedId()
        val stateId = "1"
        val differentStateId = "3"
        coEvery{ projectRepository.getProjectById(any()) } returns createProject(states = listOf(createState(id = differentStateId)))

        assertThrows<StateNotFoundException> {
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
