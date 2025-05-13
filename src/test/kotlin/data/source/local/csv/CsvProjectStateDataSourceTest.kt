package org.example.data.source.local.csv

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.logic.models.ProjectState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CsvProjectStateDataSourceTest {

    private lateinit var csvReader: CSVReader
    private lateinit var csvWriter: CSVWriter
    private lateinit var dataSource: CsvProjectStateDataSource

    private val projectId = Uuid.random()
    private val state1 = ProjectState(Uuid.random(), "To Do", projectId)
    private val state2 = ProjectState(Uuid.random(), "In Progress", projectId)

    @BeforeEach
    fun setup() {
        csvReader = mockk()
        csvWriter = mockk(relaxed = true)
        every { csvReader.readLines() } returns emptyList()
        dataSource = CsvProjectStateDataSource(csvReader, csvWriter)
    }

    @Test
    fun `should return empty list when project state is deleted by id`() {
        dataSource.createProjectState(state1)
        dataSource.deleteProjectState(state1.id)

        val result = dataSource.getProjectStates(projectId)
        assertThat(result).isEmpty()
        verify(exactly = 2) { csvWriter.writeLines(any()) }
    }

    @Test
    fun `should return null when project state id is not found`() {
        val result = dataSource.getProjectStateById(Uuid.random())
        assertThat(result).isNull()
    }

    @Test
    fun `should return updated state when project state exists`() {
        dataSource.createProjectState(state1)
        val updatedState = ProjectState(state1.id, "Done", projectId)

        val result = dataSource.updateProjectState(updatedState)

        assertThat(result).isEqualTo(updatedState)
    }

    @Test
    fun `should call writeLines once when project state is updated`() {
        dataSource.createProjectState(state1)
        val updatedState = ProjectState(state1.id, "Done", projectId)
        clearMocks(csvWriter)

        dataSource.updateProjectState(updatedState)

        verify(exactly = 1) { csvWriter.writeLines(any()) }
    }

    @Test
    fun `should return null and not update list when updating non-existent id`() {
        val nonExistentState = ProjectState(Uuid.random(), "Done", projectId)

        dataSource.updateProjectState(nonExistentState)

        val result = dataSource.getProjectStateById(nonExistentState.id)
        assertThat(result).isNull()
        assertThat(dataSource.getProjectStates(projectId)).isEmpty()
    }

    @Test
    fun `should return empty list when updating with no existing states`() {
        val updatedState = ProjectState(Uuid.random(), "Done", projectId)

        dataSource.updateProjectState(updatedState)

        val states = dataSource.getProjectStates(projectId)
        assertThat(states).isEmpty()
    }
}
