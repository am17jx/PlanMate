package data.utils

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.data.utils.CSVReader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.IOException

class CSVReaderTest {
    private lateinit var csvReader: CSVReader
    private lateinit var tempFile: File

    @TempDir
    lateinit var tempDir: File

    @BeforeEach
    fun setUp() {
        tempFile = File(tempDir, "test.csv")
        tempFile.writeText(
            """
            username,password,role
            mohamed1,a1234567,MATE
            ahmed1,a7654321,ADMIN
            """.trimIndent(),
        )

        csvReader = CSVReader(tempFile)
    }

    @Test
    fun `should read all CSV data as raw lines when they are available in file`() {
        val result = csvReader.readLines()

        assertThat(result).hasSize(3)
        assertThat(result[0]).isEqualTo("username,password,role")
        assertThat(result[1]).isEqualTo("mohamed1,a1234567,MATE")
        assertThat(result[2]).isEqualTo("ahmed1,a7654321,ADMIN")
    }

    @Test
    fun `should throw IOException when file is not readable`() {
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.exists() } returns true
        every { mockFile.canRead() } returns false
        every { mockFile.isDirectory() } returns false

        val exception =
            assertThrows<IOException> {
                CSVReader(mockFile)
            }

        assertThat(exception).hasMessageThat().contains(CSVReader.CANNOT_READ_FILE_ERROR_MESSAGE)
    }

    @Test
    fun `should throw IOException when directory is provided instead of file`() {
        val exception =
            assertThrows<IOException> {
                CSVReader(tempDir)
            }

        assertThat(exception).hasMessageThat().contains(CSVReader.DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE)
    }

    @AfterEach
    fun tearDown() {
        if (tempFile.exists()) {
            tempFile.delete()
        }
    }
}
