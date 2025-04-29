package data.utils

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.data.utils.CSVWriter
import org.example.data.utils.CSVWriter.Companion.DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.IOException

class CSVWriterTest {
    private lateinit var csvWriter: CSVWriter
    private lateinit var tempFile: File

    @TempDir
    lateinit var tempDir: File

    @BeforeEach
    fun setUp() {
        tempFile = File(tempDir, "test.csv")
        csvWriter = CSVWriter(tempFile)
    }

    @AfterEach
    fun tearDown() {
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }

    @Test
    fun `should create a new file when initialized with a valid path`() {
        assertThat(tempFile.exists()).isTrue()
    }

    @Test
    fun `should write raw string lines to CSV file`() {
        val lines = listOf(
            "username,password,role", "mohamed1,a1234567,MATE", "ahmed1,a7654321,ADMIN"
        )

        csvWriter.writeLines(lines)

        val content = tempFile.readText()
        val expectedContent = """
            username,password,role
            mohamed1,a1234567,MATE
            ahmed1,a7654321,ADMIN
        """.trimIndent().replace("\n", System.lineSeparator())

        assertThat(content).isEqualTo(expectedContent)
    }

    @Test
    fun `should throw IllegalArgumentException when writing empty lines`() {
        val emptyLines = emptyList<String>()

        val exception = assertThrows<IllegalArgumentException> {
            csvWriter.writeLines(emptyLines)
        }

        assertThat(exception).hasMessageThat().contains(CSVWriter.EMPTY_LINES_ERROR_MESSAGE)
    }

    @Test
    fun `should throw exception when directory is provided instead of file`() {
        val exception = assertThrows<IOException> {
            CSVWriter(tempDir)
        }

        assertThat(exception).hasMessageThat().contains(CSVWriter.DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE)
    }

    @Test
    fun `should throw exception when file is not writable`() {
        val mockFile = mockk<File>()
        every { mockFile.exists() } returns true
        every { mockFile.canWrite() } returns false

        val exception = assertThrows<IOException> {
            CSVWriter(mockFile)
        }
        assertThat(exception).hasMessageThat().contains(CSVWriter.CANNOT_WRITE_TO_FILE_ERROR_MESSAGE)
    }
}