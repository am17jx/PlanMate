package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.Task
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.UpdateTaskUseCase
import org.example.logic.utils.TaskNotChangedException
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateTaskUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var useCase: UpdateTaskUseCase

    private val user = User(Uuid.random(), "Sarah", UserRole.USER, User.AuthenticationMethod.Password("password"))

    @BeforeEach
    fun setup() {
        createAuditLogUseCase = mockk(relaxed = true)
        taskRepository = mockk(relaxed = true)
        useCase = UpdateTaskUseCase(taskRepository, createAuditLogUseCase)
    }

    @Test
    fun `should return updated task when task is modified`() =
        runTest {
            val id = Uuid.random()
            val oldTask = Task(id, "Old", id, "u1", id, "", id)
            val newTask = oldTask.copy(name = "Updated")
            coEvery { taskRepository.getTaskById(id) } returns oldTask
            coEvery { taskRepository.updateTask(newTask) } returns newTask
            coEvery { createAuditLogUseCase.logCreation(any(), any(), any()) } returnsArgument 0

            val result =
                useCase(
                    Task(
                        id,
                        "Updated",
                        id,
                        "u1",
                        id,
                        "",
                        id,
                    ),
                )

            assertThat(result.id, equalTo(newTask.id))
            assertThat(result.name, equalTo(newTask.name))

            coVerify { taskRepository.updateTask(newTask) }
        }

    @Test
    fun `should throw TaskNotChangedException when task is not modified`() =
        runTest {
            val id = Uuid.random()
            val task = Task(id, "Old", id, "u1", id, "", id)
            val updatedTask = Task(id, "Old", id, "u1", id, "", id)
            coEvery { taskRepository.getTaskById(any()) } returns task

            assertThrows<TaskNotChangedException> {
                useCase(
                    updatedTask,
                )
            }
        }

    @Test
    fun `should return updated task when only stateId is changed`() =
        runTest {
            val id = Uuid.random()
            val stateId = Uuid.random()
            val oldTask = Task(id, "Old", id, "u1", id, "", id)
            val newTask = oldTask.copy(stateId = stateId)
            coEvery { taskRepository.getTaskById(Uuid.random()) } returns oldTask
            coEvery { taskRepository.updateTask(newTask) } returns newTask

            val result = useCase(newTask)

            assertThat(result.stateId, equalTo(stateId))
        }

    @Test
    fun `should return updated task and create audit log when both name and stateId are changed`() =
        runTest {
            val id = Uuid.random()
            val stateId = Uuid.random()
            val oldTask = Task(id, "Old", id, "u1", id, "", id)
            val newTask = oldTask.copy(name = "New Task", stateId = stateId)

            coEvery { taskRepository.getTaskById(Uuid.random()) } returns oldTask
            coEvery { taskRepository.updateTask(newTask) } returns newTask

            val result =
                useCase(newTask)

            assertThat(result.id).isEqualTo(newTask.id)
            assertThat(result.name).isEqualTo(newTask.name)
        }

    @Test
    fun `should return updated task when user is logged in`() =
        runTest {
            val oldTask = Task(Uuid.random(), "Old", Uuid.random(), "u1", Uuid.random(), "", Uuid.random())
            val newTask = oldTask.copy(name = "Updated Task", stateId = Uuid.random())

            coEvery { taskRepository.getTaskById(Uuid.random()) } returns oldTask
            coEvery { taskRepository.updateTask(newTask) } returns newTask

            val result =
                useCase(newTask)

            assertThat(result.id).isEqualTo(newTask.id)
            assertThat(result.name).isEqualTo(newTask.name)
        }
}
