package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.source.local.contract.LocalProjectDataSource
import org.example.logic.models.Project
import org.example.logic.models.State
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectRepositoryImplTest {
    private lateinit var mockLocalDataSource: LocalProjectDataSource
    private lateinit var repository: ProjectRepositoryImpl
    private lateinit var testProjects: List<Project>

    @BeforeEach
    fun setUp() {
        mockLocalDataSource = mockk(relaxed = true)

        testProjects =
            listOf(
                Project(
                    id = "1",
                    name = "Project 1",
                    states = listOf(State(id = "1", title = "To Do")),
                    auditLogsIds = listOf("100"),
                ),
                Project(
                    id = "2",
                    name = "Project 2",
                    states = listOf(State(id = "2", title = "In Progress")),
                    auditLogsIds = listOf("200"),
                ),
            )
        every { mockLocalDataSource.getAllProjects() } returns testProjects
        repository = ProjectRepositoryImpl(mockLocalDataSource)
    }

    @Test
    fun `createProject should delegates to localDataSource and returns result`() {
        val newProject =
            Project(
                id = "3",
                name = "Project 3",
                states = listOf(State(id = "3", title = "Done")),
                auditLogsIds = listOf("300"),
            )
        every { mockLocalDataSource.createProject(any()) } returns newProject

        val result = repository.createProject(newProject)

        verify(exactly = 1) { mockLocalDataSource.createProject(newProject) }
        assertThat(result).isEqualTo(newProject)
    }

    @Test
    fun `updateProject should delegates to localDataSource and returns result`() {
        val updatedProject =
            Project(
                id = "1",
                name = "Updated Project 1",
                states = listOf(State(id = "1", title = "Updated State")),
                auditLogsIds = listOf("100", "101"),
            )
        every { mockLocalDataSource.updateProject(any()) } returns updatedProject

        val result = repository.updateProject(updatedProject)

        verify(exactly = 1) { mockLocalDataSource.updateProject(updatedProject) }
        assertThat(result).isEqualTo(updatedProject)
    }

    @Test
    fun `deleteProject should delegates to localDataSource`() {
        val projectIdToDelete = "1"
        every { mockLocalDataSource.deleteProject(any()) } just runs

        repository.deleteProject(projectIdToDelete)

        verify(exactly = 1) { mockLocalDataSource.deleteProject(projectIdToDelete) }
    }

    @Test
    fun `getAllProjects should delegates to localDataSource and returns result`() {
        val result = repository.getAllProjects()

        verify(exactly = 1) { mockLocalDataSource.getAllProjects() }
        assertThat(result).isEqualTo(testProjects)
        assertThat(result).hasSize(2)
    }

    @Test
    fun `getProjectById should returns project when found`() {
        val projectId = "1"
        every { mockLocalDataSource.getProjectById(any()) } returns
            Project(
                id = "1",
                name = "Project 1",
                states = listOf(State(id = "1", title = "To Do")),
                auditLogsIds = listOf("100"),
            )

        val result = repository.getProjectById(projectId)

        verify(exactly = 1) { mockLocalDataSource.getProjectById(projectId) }
        assertThat(result).isNotNull()
        assertThat(result.id).isEqualTo("1")
        assertThat(result.name).isEqualTo("Project 1")
    }
}
