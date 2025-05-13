package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createTask
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetTasksByProjectStateUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetTasksByProjectStateUseCaseTest {

 private lateinit var taskRepository: TaskRepository
 private lateinit var getTasksByProjectStateUseCase: GetTasksByProjectStateUseCase
 private val stateId = Uuid.random()

 @BeforeEach
 fun setUp() {
  taskRepository = mockk(relaxed = true)
  getTasksByProjectStateUseCase = GetTasksByProjectStateUseCase(taskRepository)
 }

 @Test
 fun `should return tasks when tasks exist for the given state`() = runTest {
  val tasks = listOf(
   createTask(stateId, "task1"),
   createTask(stateId, "task2")
  )
  coEvery { taskRepository.getTasksByProjectState(stateId) } returns tasks

  val result = getTasksByProjectStateUseCase(stateId)

  assertThat(result).isEqualTo(tasks)
 }

 @Test
 fun `should return empty list when no tasks exist for the given state`() = runTest {
  coEvery { taskRepository.getTasksByProjectState(stateId) } returns emptyList()

  val result = getTasksByProjectStateUseCase(stateId)

  assertThat(result).isEmpty()
 }
}
