package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createTask
import mockdata.createUser
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.Validation
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.TaskStateNotFoundException
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
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var projectStateRepository: ProjectStateRepository
    private lateinit var validation: Validation
    private lateinit var createTaskUseCase: CreateTaskUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        getCurrentUserUseCase = mockk(relaxed = true)
        createAuditLogUseCase = mockk(relaxed = true)
        projectStateRepository = mockk(relaxed = true)
        validation = mockk(relaxed = true)
        createTaskUseCase =
            CreateTaskUseCase(
                taskRepository,
                getCurrentUserUseCase,
                createAuditLogUseCase,
                projectStateRepository,
                validation,
            )
    }

    @Test
    fun `should return created task when there is no blank input parameters and project and state exist`() =
        runTest {
            val taskName = "Write CreateTaskUseCase test cases"
            val projectId = Uuid.random()
            val stateId = Uuid.random()
            coEvery { getCurrentUserUseCase() } returns createUser()
            coEvery { taskRepository.createTask(any()) } returns
                createTask(
                    name = taskName,
                    projectId = projectId,
                    stateId = stateId,
                )

            val result = createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)

            coVerify { getCurrentUserUseCase() }
            coVerify { taskRepository.createTask(any()) }
            assertThat(result.name).isEqualTo(taskName)
            assertThat(result.projectId).isEqualTo(projectId)
            assertThat(result.stateId).isEqualTo(stateId)
        }

    @ParameterizedTest
    @MethodSource("provideBlankInputScenarios")
    fun `should throw BlankInputException when any of the inputs is blank`(
        taskName: String,
        projectId: Uuid,
        stateId: Uuid,
    ) = runTest {
        coEvery { validation.validateInputNotBlankOrThrow(any()) } throws BlankInputException()

        assertThrows<BlankInputException> {
            createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)
        }
    }

    @Test
    fun `should throw TaskStateNotFoundException when state doesn't exist`() =
        runTest {
            val taskName = "Test"
            val projectId = Uuid.random()
            val stateId = Uuid.random()
            coEvery { projectStateRepository.getProjectStateById(any()) } throws TaskStateNotFoundException()

            assertThrows<TaskStateNotFoundException> {
                createTaskUseCase(name = taskName, projectId = projectId, stateId = stateId)
            }
        }

    companion object {
        @JvmStatic
        fun provideBlankInputScenarios(): Stream<Arguments> =
            Stream.of(
                Arguments.argumentSet(
                    "blank task name",
                    "",
                    Uuid.random(),
                    Uuid.random(),
                ),
            )
    }
}
