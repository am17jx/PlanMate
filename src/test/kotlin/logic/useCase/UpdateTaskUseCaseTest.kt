package org.example.logic.usecases

import io.mockk.*
import org.example.logic.models.*
import org.example.logic.repositries.*
import org.example.logic.utils.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var authRepository: AuthenticationRepository
    private lateinit var auditRepository: AuditLogRepository
    private lateinit var useCase: UpdateTaskUseCase

    private val user = User("u1", "Sarah", "hashed", UserRole.USER)

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        authRepository = mockk()
        auditRepository = mockk()
        useCase = UpdateTaskUseCase(taskRepository, authRepository, auditRepository)
    }

    @Test
    fun `should return updated task when task is modified`() {
        val oldTask = Task("t1", "Old", "s1", "u1", emptyList(), "p1")
        val newTask = oldTask.copy(name = "Updated")

        every { taskRepository.getTaskById("t1") } returns oldTask
        every { taskRepository.updateTask(newTask) } returns newTask
        every { authRepository.getCurrentUser() } returns user
        every { auditRepository.createAuditLog(any()) } returnsArgument 0

        val result = useCase("t1", newTask)

        assertThat(result, equalTo(newTask))
        verify { auditRepository.createAuditLog(any()) }
    }

    @Test
    fun `should throw TaskNotChangedException when task is not modified`() {
        val task = Task("t2", "Same", "s1", "u1", emptyList(), "p1")

        every { taskRepository.getTaskById("t2") } returns task

        val exception = assertThrows<TaskNotChangedException> {
            useCase("t2", task)
        }

        assertThat(exception.message, containsString("No changes detected"))
    }

    @Test
    fun `should throw TaskNotFoundException when task does not exist`() {
        every { taskRepository.getTaskById("t3") } returns null

        val exception = assertThrows<TaskNotFoundException> {
            useCase("t3", Task("t3", "Missing", "s1", "u1", emptyList(), "p1"))
        }

        assertThat(exception.message, containsString("not found"))
    }



    @Test
    fun `should return updated task when only stateId is changed`() {
        val oldTask = Task("t4", "Do Something", "s1", "u1", emptyList(), "p1")
        val newTask = oldTask.copy(stateId = "s2")

        every { taskRepository.getTaskById("t4") } returns oldTask
        every { taskRepository.updateTask(newTask) } returns newTask
        every { authRepository.getCurrentUser() } returns user
        every { auditRepository.createAuditLog(any()) } returnsArgument 0

        val result = useCase("t4", newTask)

        assertThat(result.stateId, equalTo("s2"))
        verify { auditRepository.createAuditLog(any()) }
    }

    @Test
    fun `should return updated task and create audit log when both name and stateId are changed`() {
        val oldTask = Task("t5", "Old Task", "s1", "u1", emptyList(), "p1")
        val newTask = oldTask.copy(name = "New Task", stateId = "s3")

        every { taskRepository.getTaskById("t5") } returns oldTask
        every { taskRepository.updateTask(newTask) } returns newTask
        every { authRepository.getCurrentUser() } returns user

        val auditSlot = slot<AuditLog>()
        every { auditRepository.createAuditLog(capture(auditSlot)) } returnsArgument 0

        val result = useCase("t5", newTask)

        assertThat(result.name, equalTo("New Task"))
        assertThat(result.stateId, equalTo("s3"))

        val log = auditSlot.captured
        assertThat(log.userId, equalTo(user.id))
        assertThat(log.entityType, equalTo(AuditLogEntityType.TASK))
        assertThat(log.actionType, equalTo(AuditLogActionType.UPDATE))
    }

    @Test
    fun `should throw TaskNotFoundException when task is null on retrieval`() {
        every { taskRepository.getTaskById("t6") } returns null

        val exception = assertThrows<TaskNotFoundException> {
            useCase("t6", Task("t6", "Name", "s1", "u1", emptyList(), "p1"))
        }

        assertThat(exception.message, containsString("Task with id t6 not found"))
        verify(exactly = 1) { taskRepository.getTaskById("t6") }
    }

}
