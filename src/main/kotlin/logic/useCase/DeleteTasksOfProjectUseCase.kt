package org.example.logic.useCase


class DeleteTasksOfProjectUseCase(
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getProjectTasksUseCase: GetProjectTasksUseCase,
) {
    operator fun invoke(projectId: String){
        getProjectTasksUseCase(projectId).forEach { deleteTaskUseCase(it.id) }
    }

}
