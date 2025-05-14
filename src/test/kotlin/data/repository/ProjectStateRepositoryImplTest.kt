package data.repository

import org.junit.jupiter.api.Assertions.*

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.repository.ProjectStateRepositoryImpl
import org.example.data.source.remote.contract.RemoteProjectStateDataSource
import org.example.logic.models.ProjectState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProjectStateRepositoryImplTest {
    private lateinit var repository: ProjectStateRepositoryImpl
    private lateinit var remoteDataSource: RemoteProjectStateDataSource
    private val projectId1 = Uuid.random()
    private  val projectState = ProjectState(
        id = Uuid.random(),
        title = "ToDO",
        projectId = projectId1
    )
    @BeforeEach
    fun setup() {
        remoteDataSource = mockk(relaxed = true)
        repository = ProjectStateRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `createProjectState should return created project state when a new project state created`() = runTest {
        
        coEvery { remoteDataSource.createProjectState(projectState) } returns projectState

        
        val result = repository.createProjectState(projectState)

       
        assertEquals(projectState, result)
        coVerify(exactly = 1) { remoteDataSource.createProjectState(projectState) }
    }

    @Test
    fun `createProjectState should throws exception when remote source fails`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { remoteDataSource.createProjectState(projectState) } throws exception

         
        assertThrows<RuntimeException> {
            repository.createProjectState(projectState)
        }
    }

    @Test
    fun `updateProjectState should return updated project state when an old project state updated`() = runTest {

        coEvery { remoteDataSource.updateProjectState(projectState) } returns projectState

        
        val result = repository.updateProjectState(projectState)

        
        assertEquals(projectState, result)
        coVerify(exactly = 1) { remoteDataSource.updateProjectState(projectState) }
    }

    @Test
    fun `updateProjectState should throws exception when remote source fails`()= runTest {

        val exception = RuntimeException("Network error")
        coEvery { remoteDataSource.updateProjectState(projectState) } throws exception

         
        assertThrows<RuntimeException> {
            repository.updateProjectState(projectState)
        }
    }

    @Test
    fun `deleteProjectState should call remote source when deleting a project state`() = runTest {
        
        val projectStateId = Uuid.random()
        coEvery { remoteDataSource.deleteProjectState(projectStateId) } returns Unit

        
        repository.deleteProjectState(projectStateId)

        
        coVerify(exactly = 1) { remoteDataSource.deleteProjectState(projectStateId) }
    }

    @Test
    fun `deleteProjectState should throws exception when remote source fails`() = runTest {
        
        val projectStateId = Uuid.random()
        val exception = RuntimeException("Network error")
        coEvery { remoteDataSource.deleteProjectState(projectStateId) } throws exception

         
        assertThrows<RuntimeException> {
            repository.deleteProjectState(projectStateId)
        }
    }

    @Test
    fun `getProjectStates should return list of project states`() = runTest {
        
        val projectId = Uuid.random()
        val projectStates = listOf(projectState, projectState.copy(title = "RUN"))
        coEvery { remoteDataSource.getProjectStates(projectId) } returns projectStates

        
        val result = repository.getProjectStates(projectId)

        
        assertEquals(projectStates, result)
        coVerify(exactly = 1) { remoteDataSource.getProjectStates(projectId) }
    }

    @Test
    fun `getProjectStates should return empty list when no states exist`() = runTest {
        
        val projectId = Uuid.random()
        coEvery { remoteDataSource.getProjectStates(projectId) } returns emptyList()

        
        val result = repository.getProjectStates(projectId)

        
        assertEquals(emptyList(), result)
    }

    @Test
    fun `getProjectStateById should return project state when exists`() = runTest {
        
        val projectStateId = Uuid.random()
        coEvery { remoteDataSource.getProjectStateById(projectStateId) } returns projectState

        
        val result = repository.getProjectStateById(projectStateId)

        
        assertEquals(projectState, result)
        coVerify(exactly = 1) { remoteDataSource.getProjectStateById(projectStateId) }
    }

    @Test
    fun `getProjectStateById should return null when project state doesn't exist`() = runTest {
        
        val projectStateId = Uuid.random()
        coEvery { remoteDataSource.getProjectStateById(projectStateId) } returns null

        
        val result = repository.getProjectStateById(projectStateId)

        
        assertNull(result)
    }
} 