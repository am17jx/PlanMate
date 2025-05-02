package data.source.local

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.example.data.source.local.CsvProjectDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.example.data.utils.mapper.toCsvLines
import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.utils.ProjectCreationFailedException
import org.example.logic.utils.ProjectNotChangedException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

class CsvProjectDataSourceTest {
    private lateinit var mockCsvReader: CSVReader
    private lateinit var mockCsvWriter: CSVWriter
    private lateinit var dataSource: CsvProjectDataSource
    private lateinit var testProjects: List<Project>
    private lateinit var testCsvLines: List<String>

    @BeforeEach
    fun setUp() {
        mockCsvReader = mockk(relaxed = true)
        mockCsvWriter = mockk(relaxed = true)

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
        testCsvLines = testProjects.toCsvLines()

        every { mockCsvReader.readLines() } returns testCsvLines
        every { mockCsvWriter.writeLines(any()) } just runs

        dataSource = CsvProjectDataSource(mockCsvReader, mockCsvWriter)
    }

    @Test
    fun `initialization loads projects from file`() {
        verify(exactly = 1) { mockCsvReader.readLines() }

        val allProjects = dataSource.getAllProjects()
        assertThat(allProjects).hasSize(2)
        assertThat(allProjects[0].id).isEqualTo("1")
        assertThat(allProjects[1].id).isEqualTo("2")
    }

    @Test
    fun `createProject should adds the new project and saves to file`() {
        val newProject =
            Project(
                id = "3",
                name = "Project 3",
                states = listOf(State(id = "3", title = "Done")),
                auditLogsIds = listOf("300"),
            )

        val result = dataSource.createProject(newProject)

        assertThat(result).isEqualTo(newProject)
        verify { mockCsvWriter.writeLines(any()) }
        val allProjects = dataSource.getAllProjects()
        assertThat(allProjects).hasSize(3)
        assertThat(allProjects).contains(newProject)
    }

    @Test
    fun `createProject should throws ProjectCreationFailedException when saving fails`() {
        val newProject =
            Project(
                id = "3",
                name = "Project 3",
                states = emptyList(),
                auditLogsIds = emptyList(),
            )
        every { mockCsvWriter.writeLines(any()) } throws IOException()

        assertThrows<ProjectCreationFailedException> {
            dataSource.createProject(newProject)
        }
    }

    @Test
    fun `updateProject should updates existing project and saves to file`() {
        val updatedProject =
            Project(
                id = "1",
                name = "Updated Project 1",
                states = listOf(State(id = "1", title = "Updated State")),
                auditLogsIds = listOf("100", "101"),
            )

        val result = dataSource.updateProject(updatedProject)

        assertThat(result).isEqualTo(updatedProject)
        verify { mockCsvWriter.writeLines(any()) }
        val allProjects = dataSource.getAllProjects()
        assertThat(allProjects).contains(updatedProject)
    }

    @Test
    fun `updateProject should throws ProjectNotChangedException when saving fails`() {
        val updatedProject =
            Project(
                id = "1",
                name = "Updated Project 1",
                states = emptyList(),
                auditLogsIds = emptyList(),
            )
        every { mockCsvWriter.writeLines(any()) } throws IOException("Test exception")

        assertThrows<ProjectNotChangedException> {
            dataSource.updateProject(updatedProject)
        }
    }

    @Test
    fun `deleteProject should removes project and saves to file`() {
        val projectIdToDelete = "1"

        dataSource.deleteProject(projectIdToDelete)

        verify { mockCsvWriter.writeLines(any()) }
        val allProjects = dataSource.getAllProjects()
        assertThat(allProjects).hasSize(1)
        assertThat(allProjects[0].id).isEqualTo("2")
    }

    @Test
    fun `getAllProjects should returns all projects`() {
        val result = dataSource.getAllProjects()

        assertThat(result).hasSize(2)
        assertThat(result[0].id).isEqualTo("1")
        assertThat(result[0].name).isEqualTo("Project 1")
        assertThat(result[1].id).isEqualTo("2")
        assertThat(result[1].name).isEqualTo("Project 2")
    }

    @Test
    fun `getProjectById should returns correct project with the same id`() {
        val projectId = "2"

        val result = dataSource.getProjectById(projectId)

        assertThat(result?.id).isEqualTo("2")
        assertThat(result?.name).isEqualTo("Project 2")
    }
}
