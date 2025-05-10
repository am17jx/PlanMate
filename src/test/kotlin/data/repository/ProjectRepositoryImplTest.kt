package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.source.remote.RoleValidationInterceptor
import org.example.data.source.remote.contract.RemoteProjectDataSource
import org.example.logic.models.Project
import org.example.logic.models.State
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectRepositoryImplTest {
    private lateinit var mockRemoteDataSource: RemoteProjectDataSource
    private lateinit var repository: ProjectRepositoryImpl
    private lateinit var testProjects: List<Project>
    private lateinit var roleValidationInterceptor : RoleValidationInterceptor

    @BeforeEach
    fun setUp() {
        mockRemoteDataSource = mockk(relaxed = true)
        roleValidationInterceptor =  mockk(relaxed = true)

        testProjects = listOf(
            Project(
                id = "1",
                name = "Project 1",
                tasksStatesIds = listOf(State(id = "1", title = "To Do")),
                auditLogsIds = listOf("100"),
            ),
            Project(
                id = "2",
                name = "Project 2",
                tasksStatesIds = listOf(State(id = "2", title = "In Progress")),
                auditLogsIds = listOf("200"),
            ),
        )
        coEvery { mockRemoteDataSource.getAllProjects() } returns testProjects
        repository = ProjectRepositoryImpl(mockRemoteDataSource,roleValidationInterceptor)
    }

    @Test
    fun `createProject should delegates to localDataSource and returns result`() = runTest {
        val newProject = Project(
            id = "3",
            name = "Project 3",
            tasksStatesIds = listOf(State(id = "3", title = "Done")),
            auditLogsIds = listOf("300"),
        )
        coEvery { roleValidationInterceptor.validateRole<Project>(any(),any())} returns newProject

        val result = repository.createProject(newProject)

        coVerify(exactly = 1) { roleValidationInterceptor.validateRole<Project>(any(),any())}
        assertThat(result).isEqualTo(newProject)
    }

    @Test
    fun `updateProject should delegates to localDataSource and returns result`() = runTest {
        val updatedProject = Project(
            id = "1",
            name = "Updated Project 1",
            tasksStatesIds = listOf(State(id = "1", title = "Updated State")),
            auditLogsIds = listOf("100", "101"),
        )
        coEvery { roleValidationInterceptor.validateRole<Project>(any(),any()) } returns updatedProject

        val result = repository.updateProject(updatedProject)

        coVerify(exactly = 1) { roleValidationInterceptor.validateRole<Project>(any(),any()) }
        assertThat(result).isEqualTo(updatedProject)
    }



    @Test
    fun `getAllProjects should delegates to localDataSource and returns result`() = runTest {
        val result = repository.getAllProjects()

        coVerify(exactly = 1) { mockRemoteDataSource.getAllProjects() }
        assertThat(result).isEqualTo(testProjects)
        assertThat(result).hasSize(2)
    }

    @Test
    fun `getProjectById should returns project when found`() = runTest {
        val projectId = "1"
        coEvery { mockRemoteDataSource.getProjectById(any()) } returns Project(
            id = "1",
            name = "Project 1",
            tasksStatesIds = listOf(State(id = "1", title = "To Do")),
            auditLogsIds = listOf("100"),
        )

        val result = repository.getProjectById(projectId)

        coVerify(exactly = 1) { mockRemoteDataSource.getProjectById(projectId) }
        assertThat(result).isNotNull()
        assertThat(result?.id).isEqualTo("1")
        assertThat(result?.name).isEqualTo("Project 1")
    }
}
