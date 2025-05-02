package org.example.logic.useCase

import org.example.logic.command.Command
import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionCommands
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.models.Task
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.TaskRepository
import java.util.*

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository,
    private val userUseCase: GetCurrentUserUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
) {
    operator fun invoke(taskId: String) {

        val auditLog = saveAuditLog(taskId)
        val auditCommand = CreateAuditLogCommand(auditLogRepository, auditLog)
        val deleteTasks = DeleteTask(taskRepository, getTaskByIdUseCase(taskId))
        TransactionCommands(listOf(auditCommand, deleteTasks))

    }


    private fun saveAuditLog(taskId: String): AuditLog {
        val auditLog = AuditLog(
            id = UUID.randomUUID().toString(),
            userId = userUseCase().id,
            action = "${userUseCase().username} deleted task with id $taskId",
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.TASK,
            entityId = taskId,
            actionType = AuditLogActionType.DELETE
        )
        return auditLog
    }
}

class DeleteTask(
    private val taskRepository: TaskRepository,
    private val task: Task,
) : Command {
    override fun execute() {
        taskRepository.deleteTask(task.id)
    }

    override fun undo() {
        taskRepository.createTask(task)
    }


}