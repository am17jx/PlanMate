package org.example.logic.usecases

import org.example.logic.models.*
import org.example.logic.repositries.*
import org.example.logic.utils.*
import java.util.*

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val auditLogRepository: AuditLogRepository
) {

    operator fun invoke(taskId: String, updatedTask: Task): Task {
        val existingTask = getExistingTaskOrThrow(taskId)
        ensureTaskIsChanged(existingTask, updatedTask)
        val savedTask = saveUpdatedTask(updatedTask)
        val currentUser = getCurrentUserOrThrow()
        logAudit(currentUser, existingTask, savedTask)
        return savedTask
    }

    private fun getExistingTaskOrThrow(taskId: String): Task {
        return taskRepository.getTaskById(taskId)
            ?: throw TaskNotFoundException("Task with id $taskId not found")
    }

    private fun ensureTaskIsChanged(oldTask: Task, newTask: Task) {
        if (oldTask == newTask) {
            throw TaskNotChangedException("No changes detected for task with id ${newTask.id}")
        }
    }

    private fun saveUpdatedTask(task: Task): Task {
        return taskRepository.updateTask(task)
    }

    private fun getCurrentUserOrThrow(): User {
        return authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException("No logged-in user found")
    }

    private fun logAudit(user: User, oldTask: Task, newTask: Task) {
        val auditLog = AuditLog(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            action = "Updated task from stateId=${oldTask.stateId} to stateId=${newTask.stateId}",
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.TASK,
            entityId = newTask.id,
            actionType = AuditLogActionType.UPDATE
        )
        auditLogRepository.createAuditLog(auditLog)
    }
}
