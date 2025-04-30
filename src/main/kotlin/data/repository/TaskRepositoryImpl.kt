package org.example.data.repository

import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository

class TaskRepositoryImpl: TaskRepository {
    override fun createTask(task: Task): Task {
        TODO("Not yet implemented")
    }

    override fun updateTask(updatedTask: Task): Task {
        TODO("Not yet implemented")
    }

    override fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override fun getAllTasks(): List<Task> {
        TODO("Not yet implemented")
    }

    override fun getTaskById(taskId: String): Task? {
        TODO("Not yet implemented")
    }
}