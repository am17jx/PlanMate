package data.utils.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.source.local.csv.utils.mapper.toCsvLine
import org.example.data.source.local.csv.utils.mapper.toCsvLines
import org.example.data.source.local.csv.utils.mapper.toProject
import org.example.data.source.local.csv.utils.mapper.toProjectList
import org.example.logic.models.Project
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProjectCsvMapperTest {
    private val id1 = Uuid.random()
    private val id2 = Uuid.random()

    @Test
    fun `should convert project to CSV line`() {
        val project =
            Project(
                id = id1,
                name = "Test Project"
            )

        val csvLine = project.toCsvLine()

        val expectedCsvLine = "${id1.toHexString()},Test Project"
        assertThat(csvLine).isEqualTo(expectedCsvLine)
    }

    @Test
    fun `should convert CSV line to project when all 4 fields are present and each field isn't empty`() {
        val csvLine = "${id1.toHexString()},Test Project,[${id1.toHexString()},${id1.toHexString()},${id1.toHexString()},],[${id1.toHexString()},${id1.toHexString()},${id1.toHexString()},]"

        val project = csvLine.toProject()

        val expectedProject =
            Project(
                id = id1,
                name = "Test Project"
            )
        assertThat(project).isEqualTo(expectedProject)
    }

    @Test
    fun `should convert project to CSV line with empty states list and auditLogsIds list when they are empty`() {
        val project =
            Project(
                id = id1,
                name = "Empty Project"
            )

        val csvLine = project.toCsvLine()
        val convertedProject = csvLine.toProject()

        assertThat(csvLine).isEqualTo("${id1.toHexString()},Empty Project")
        assertThat(convertedProject).isEqualTo(project)
    }

    @Test
    fun `should map list of projects to list of CSV lines`() {
        val projects =
            listOf(
                Project(
                    id = id1,
                    name = "Project 1"
                ),
                Project(
                    id = id2,
                    name = "Project 2"
                ),
            )

        val csvLines = projects.toCsvLines()

        assertThat(csvLines).hasSize(2)
        assertThat(csvLines[0]).isEqualTo("${id1.toHexString()},Project 1")
        assertThat(csvLines[1]).isEqualTo("${id2.toHexString()},Project 2")
    }

    @Test
    fun `should map list of CSV lines to list of projects`() {
        val csvLines =
            listOf(
                "${id1.toHexString()},Project 1",
                "${id2.toHexString()},Project 2",
            )

        val projects = csvLines.toProjectList()

        val firstExpectedProject =
            Project(id1, "Project 1")
        val secondExpectedProject =
            Project(id2, "Project 2")
        assertThat(projects).hasSize(2)
        assertThat(projects[0]).isEqualTo(firstExpectedProject)
        assertThat(projects[1]).isEqualTo(secondExpectedProject)
    }

    @Test
    fun `should convert from and to the same Project`() {
        val project =
            Project(
                id = id1,
                name = "Complex Project"
            )

        val csvLine = project.toCsvLine()
        val convertedProject = csvLine.toProject()

        assertThat(csvLine).isEqualTo("${id1.toHexString()},Complex Project")
        assertThat(convertedProject.id).isEqualTo(id1)
        assertThat(convertedProject.name).isEqualTo("Complex Project")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "1,Test Project,,", "1,Test Project,[,,],[100,101,102]"])
    fun `should throw IllegalArgumentException when input string doesn't have enough segments`(csvLine: String) {
        assertThrows<IllegalArgumentException> {
            csvLine.toProject()
        }
    }
}
