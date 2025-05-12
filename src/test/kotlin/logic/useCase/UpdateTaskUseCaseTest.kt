package logic.useCase

import com.google.common.truth.Truth
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.logic.models.*
import org.example.logic.repositries.*
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.UpdateTaskUseCase
import org.example.logic.utils.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var auditRepository: AuditLogRepository
    private lateinit var useCase: UpdateTaskUseCase
    private lateinit var currentUserUseCase: GetCurrentUserUseCase

    private val user = User(Uuid.random(), "Sarah", UserRole.USER, User.AuthenticationMethod.Password("password"))

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        auditRepository = mockk()
        currentUserUseCase = mockk(relaxed = true)
        //useCase = UpdateTaskUseCase(taskRepository, auditRepository,currentUserUseCase)
    }

    @Test
    fun `should return updated task when task is modified`() = runTest {
        val oldTask = Task(Uuid.random(), "Old", Uuid.random(), "u1", Uuid.random(), "", Uuid.random() )
        val newTask = oldTask.copy(name = "Updated")
        coEvery { taskRepository.getTaskById(Uuid.random()) } returns oldTask
        coEvery { taskRepository.updateTask(newTask) } returns newTask
        coEvery {currentUserUseCase() } returns user
        coEvery { auditRepository.createAuditLog(any()) } returnsArgument 0

        val result = useCase(Task(
            Uuid.random(),
            "Updated",
            Uuid.random(),
            "u1",
            Uuid.random(),
            "",
            Uuid.random()
        ))

        assertThat(result.id, equalTo(newTask.id))
        assertThat(result.name, equalTo(newTask.name))
        //Truth.assertThat(result.auditLogsIds.size).isGreaterThan(0)

        coVerify { auditRepository.createAuditLog(any()) }
    }

    @Test
    fun `should throw TaskNotChangedException when task is not modified`() = runTest {
        //val task = Task("t2", "Same", "s1", "u1", emptyList(), "p1")
        val task = Task(Uuid.random(), "Old", Uuid.random(), "u1", Uuid.random(), "", Uuid.random() )


        coEvery { taskRepository.getTaskById(Uuid.random()) } returns task

        assertThrows<TaskNotChangedException> {
            useCase(Task(
                Uuid.random(),
                "Old",
                Uuid.random(),
                "u1",
                Uuid.random(),
                "",
                Uuid.random()
            ))
        }
    }

    @Test
    fun `should throw TaskNotFoundException when task does not exist`() = runTest {
        coEvery { taskRepository.getTaskById(Uuid.random()) } returns null

         assertThrows<TaskNotFoundException> {
            useCase(Task(
                Uuid.random(),
                "Updated",
                Uuid.random(),
                "u1",
                Uuid.random(),
                "",
                Uuid.random()
            ))
        }
    }

    @Test
    fun `should return updated task when only stateId is changed`() = runTest {
        val oldTask = Task(Uuid.random(), "Old", Uuid.random(), "u1", Uuid.random(), "", Uuid.random() )
        val newTask = oldTask.copy(stateId = Uuid.random())
        coEvery { taskRepository.getTaskById(Uuid.random()) } returns oldTask
        coEvery { taskRepository.updateTask(newTask) } returns newTask
        coEvery {currentUserUseCase() } returns user
        coEvery { auditRepository.createAuditLog(any()) } returnsArgument 0

        val result = useCase(Task(Uuid.random(), "Old", Uuid.random(), "u1", Uuid.random(), "", Uuid.random() ))

        assertThat(result.stateId, equalTo("s2"))
        coVerify { auditRepository.createAuditLog(any()) }
    }

    @Test
    fun `should return updated task and create audit log when both name and stateId are changed`() = runTest {
        val oldTask = Task(Uuid.random(), "Old", Uuid.random(), "u1", Uuid.random(), "", Uuid.random() )
        val newTask = oldTask.copy(name = "New Task", stateId = Uuid.random())

        coEvery { taskRepository.getTaskById(Uuid.random()) } returns oldTask
        coEvery { taskRepository.updateTask(newTask) } returns newTask
        coEvery { currentUserUseCase() } returns user
        val auditSlot = slot<AuditLog>()
        coEvery { auditRepository.createAuditLog(capture(auditSlot)) } returnsArgument 0

        val result = useCase(Task(
            Uuid.random(),
            "Updated",
            Uuid.random(),
            "u1",
            Uuid.random(),
            "",
            Uuid.random()
        ))

        assertThat(result.id, equalTo(newTask.id))
        assertThat(result.name, equalTo(newTask.name))
        //Truth.assertThat(result.auditLogsIds.size).isGreaterThan(0)

        with(auditSlot.captured) {
            assertThat(userId, equalTo(user.id))
//            assertThat(entityType, equalTo(AuditLogEntityType.TASK))
//            assertThat(actionType, equalTo(AuditLogActionType.UPDATE))
        }
    }

    @Test
    fun `should throw TaskNotFoundException when task is null on retrieval`() = runTest {
        coEvery { taskRepository.getTaskById(Uuid.random()) } returns null

        assertThrows<TaskNotFoundException> {
            useCase(Task(
                Uuid.random(),
                "Updated",
                Uuid.random(),
                "u1",
                Uuid.random(),
                "",
                Uuid.random()
            ))
        }
    }

    @Test
    fun `should return updated task when user is logged in`() = runTest {
        val oldTask = Task(Uuid.random(), "Old", Uuid.random(), "u1", Uuid.random(), "", Uuid.random() )
        val newTask = oldTask.copy(name = "Updated Task", stateId = Uuid.random())

        coEvery { taskRepository.getTaskById(Uuid.random()) } returns oldTask
        coEvery { taskRepository.updateTask(newTask) } returns newTask
        coEvery {currentUserUseCase() } returns user
        coEvery { auditRepository.createAuditLog(any()) } returnsArgument 0

        val result = useCase(Task(
            Uuid.random(),
            "Updated",
            Uuid.random(),
            "u1",
            Uuid.random(),
            "",
            Uuid.random()
        ))

        assertThat(result.id, equalTo(newTask.id))
        assertThat(result.name, equalTo(newTask.name))
        //Truth.assertThat(result.auditLogsIds.size).isGreaterThan(0)

        coEvery { auditRepository.createAuditLog(any()) }
    }

    @Test
    fun `should throw NoLoggedInUserException when no user is logged in`() = runTest {
        val oldTask = Task(Uuid.random(), "Old", Uuid.random(), "u1", Uuid.random(), "", Uuid.random() )
        val updatedTask = oldTask.copy(name = "Updated Task") // to make it different

        coEvery { taskRepository.getTaskById(Uuid.random()) } returns oldTask
        coEvery { taskRepository.updateTask(updatedTask) } returns updatedTask
        coEvery {currentUserUseCase() } throws  NoLoggedInUserException()

        assertThrows<NoLoggedInUserException> {
            useCase(Task(
                Uuid.random(),
                "Updated",
                Uuid.random(),
                "u1",
                Uuid.random(),
                "",
                Uuid.random()
            ))
        }
    }

}
