package logic.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.ProjectState
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.GetProjectStatesUseCase
import org.example.logic.utils.ProjectNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetProjectStatesUseCaseTest {
 private lateinit var projectStateRepository: ProjectStateRepository
 private lateinit var getProjectStatesUseCase: GetProjectStatesUseCase

 private val projectId = Uuid.random()
 private val states = listOf(
  ProjectState(
   id = Uuid.random(),
   title = "To Do",
   projectId = projectId
  ),
  ProjectState(
   id = Uuid.random(),
   title = "In Progress",
   projectId = projectId
  ),
  ProjectState(
   id = Uuid.random(),
   title = "Done",
   projectId = projectId
  )
 )

 @BeforeEach
 fun setUp() {
  projectStateRepository = mockk(relaxed = true)
  getProjectStatesUseCase = GetProjectStatesUseCase(projectStateRepository)
 }

 @Test
 fun `should return list of project states when project exists`() = runTest {
  
  coEvery { projectStateRepository.getProjectStates(projectId) } returns states

  
  val result = getProjectStatesUseCase(projectId)

  
  assertEquals(states, result)
  coVerify(exactly = 1) { projectStateRepository.getProjectStates(projectId) }
 }

 @Test
 fun `should return empty list when project has no states`() = runTest {
  
  coEvery { projectStateRepository.getProjectStates(projectId) } returns emptyList()

  
  val result = getProjectStatesUseCase(projectId)

  
  assertEquals(emptyList<ProjectState>(), result)
  coVerify(exactly = 1) { projectStateRepository.getProjectStates(projectId) }
 }

 @Test
 fun `should propagate repository exceptions`() = runTest {
  
  coEvery { projectStateRepository.getProjectStates(projectId) } throws ProjectNotFoundException()

  
  assertThrows<ProjectNotFoundException> {
   getProjectStatesUseCase(projectId)
  }
  coVerify(exactly = 1) { projectStateRepository.getProjectStates(projectId) }
 }

 @Test
 fun `should handle repository runtime exceptions`() = runTest {
  
  val errorMessage = "Database connection failed"
  coEvery { projectStateRepository.getProjectStates(projectId) } throws RuntimeException(errorMessage)

 
  val exception = assertThrows<RuntimeException> {
   getProjectStatesUseCase(projectId)
  }
  assertEquals(errorMessage, exception.message)
  coVerify(exactly = 1) { projectStateRepository.getProjectStates(projectId) }
 }
}