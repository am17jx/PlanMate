package org.example.logic.useCase.updateTask

import kotlinx.datetime.Clock
import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionalCommand
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.NoLoggedInUserException
import org.example.logic.utils.TaskNotChangedException
import org.example.logic.utils.TaskNotFoundException
import org.example.logic.utils.formattedString
import java.util.*

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val auditLogRepository: AuditLogRepository
) {

    operator suspend fun invoke(taskId: String, updatedTask: Task): Task {
        val existingTask = getExistingTaskOrThrow(taskId)
        ensureTaskIsChanged(existingTask, updatedTask)
        val currentUser = getCurrentUserOrThrow()
        return updateAndLogTask(existingTask, updatedTask, currentUser)
    }

    private fun updateAndLogTask(oldTask: Task, updatedTask: Task, currentUser: User): Task {

        val taskAuditLog = logAudit(currentUser, updatedTask, oldTask)

        val auditCommand = CreateAuditLogCommand(auditLogRepository, taskAuditLog)
        val taskUpdatedCommand = TaskUpdateCommand(taskRepository, updatedTask, oldTask)
        val updateTaskCommandTransaction = TransactionalCommand(
            listOf(taskUpdatedCommand, auditCommand), TaskNotChangedException("Task Not changed")
        )
        try {
            updateTaskCommandTransaction.execute()
        } catch (e: TaskNotChangedException) {
            throw e
        }

        return updatedTask.copy(auditLogsIds = oldTask.auditLogsIds.plus(auditCommand.getCreatedLog()?.id ?: ""))

    }

    private fun logAudit(user: User, oldTask: Task, newTask: Task): AuditLog {
        val timestampNow = Clock.System.now()
        return AuditLog(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            action = "Updated task from stateId=${oldTask.stateId} to stateId=${newTask.stateId} at ${timestampNow.formattedString()}",
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.TASK,
            entityId = newTask.id,
            actionType = AuditLogActionType.UPDATE
        )
    }

    private suspend fun getExistingTaskOrThrow(taskId: String): Task {
        return taskRepository.getTaskById(taskId) ?: throw TaskNotFoundException("Task with id $taskId not found")
    }

    private fun ensureTaskIsChanged(oldTask: Task, newTask: Task) {
        if (oldTask == newTask) {
            throw TaskNotChangedException("No changes detected for task with id ${newTask.id}")
        }
    }

    private suspend fun getCurrentUserOrThrow(): User {
        return authenticationRepository.getCurrentUser() ?: throw NoLoggedInUserException("No logged-in user found")
    }

}
