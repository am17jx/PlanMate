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

class ProjectCsvMapperTest {
    @Test
    fun `should convert project to CSV line`() {
        val project =
            Project(
                id = "1",
                name = "Test Project",
                projectStateIds =
                    listOf("1", "2", "3"),
                auditLogsIds = listOf("100", "101", "102"),
            )

        val csvLine = project.toCsvLine()

        val expectedCsvLine = "1,Test Project,[1:To Do,2:In Progress,3:Done],[100,101,102]"
        assertThat(csvLine).isEqualTo(expectedCsvLine)
    }

    @Test
    fun `should convert CSV line to project when all 4 fields are present and each field isn't empty`() {
        val csvLine = "1,Test Project,[1:To Do,2:In Progress,3:Done],[100,101,102]"

        val project = csvLine.toProject()

        val expectedProject =
            Project(
                id = "1",
                name = "Test Project",
                projectStateIds =
                    listOf("1", "2", "3"),
                auditLogsIds = listOf("100", "101", "102"),
            )
        assertThat(project).isEqualTo(expectedProject)
    }

    @Test
    fun `should convert project to CSV line with empty states list and auditLogsIds list when they are empty`() {
        val project =
            Project(
                id = "2",
                name = "Empty Project",
                projectStateIds = emptyList(),
                auditLogsIds = emptyList(),
            )

        val csvLine = project.toCsvLine()
        val convertedProject = csvLine.toProject()

        assertThat(csvLine).isEqualTo("2,Empty Project,[],[]")
        assertThat(convertedProject).isEqualTo(project)
    }

    @Test
    fun `should map list of projects to list of CSV lines`() {
        val projects =
            listOf(
                Project(
                    id = "1",
                    name = "Project 1",
                    projectStateIds = listOf("1", "2", "3"),

                    auditLogsIds = listOf("100"),
                ),
                Project(
                    id = "2",
                    name = "Project 2",
                    projectStateIds =  listOf("1", "2", "3"),
                    auditLogsIds = listOf("200"),
                ),
            )

        val csvLines = projects.toCsvLines()

        assertThat(csvLines).hasSize(2)
        assertThat(csvLines[0]).isEqualTo("1,Project 1,[1:Task 1],[100]")
        assertThat(csvLines[1]).isEqualTo("2,Project 2,[2:Task 2],[200]")
    }

    @Test
    fun `should map list of CSV lines to list of projects`() {
        val csvLines =
            listOf(
                "1,Project 1,[1:Task 1],[100]",
                "2,Project 2,[2:Task 2],[200]",
            )

        val projects = csvLines.toProjectList()

        val firstExpectedProject =
            Project("1", "Project 1", listOf("1", "2", "3"), listOf("100"))
        val secondExpectedProject =
            Project("2", "Project 2",  listOf("1", "2", "3"), listOf("200"))
        assertThat(projects).hasSize(2)
        assertThat(projects[0]).isEqualTo(firstExpectedProject)
        assertThat(projects[1]).isEqualTo(secondExpectedProject)
    }

    @Test
    fun `should convert from and to the same Project`() {
        val project =
            Project(
                id = "4",
                name = "Complex Project",
                projectStateIds =
                    listOf("1", "2", "3"),
                auditLogsIds = listOf("300", "301"),
            )

        val csvLine = project.toCsvLine()
        val convertedProject = csvLine.toProject()

        assertThat(csvLine).isEqualTo("4,Complex Project,[1:To Do,2:In Progress,3:Ready for Review],[300,301]")
        assertThat(convertedProject.id).isEqualTo("4")
        assertThat(convertedProject.name).isEqualTo("Complex Project")
        assertThat(convertedProject.projectStateIds).hasSize(3)
        assertThat(convertedProject.projectStateIds[2]).isEqualTo("3")
        assertThat(convertedProject.auditLogsIds).containsExactly("300", "301").inOrder()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "1,Test Project,,", "1,Test Project,[,,],[100,101,102]"])
    fun `should throw IllegalArgumentException when input string doesn't have enough segments`(csvLine: String) {
        assertThrows<IllegalArgumentException> {
            csvLine.toProject()
        }
    }
}
