import com.google.common.truth.Truth.assertThat
import org.example.logic.models.UserRole
import org.example.presentation.screens.AdminHomeUI
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import presentation.utils.io.ConsoleReader
import presentation.utils.io.Viewer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class AdminHomeUITest {

    private val originalOut = System.out
    private val originalIn = System.`in`
    private lateinit var outContent: ByteArrayOutputStream

    private var navigateToShowAllProjectsCalled = false
    private var navigateToCreateProjectCalled = false
    private var navigateToCreateUserCalled = false
    private var navigateToBackCalled = false

    @BeforeEach
    fun setUp() {
        outContent = ByteArrayOutputStream()
        System.setOut(PrintStream(outContent))

        navigateToShowAllProjectsCalled = false
        navigateToCreateProjectCalled = false
        navigateToCreateUserCalled = false
        navigateToBackCalled = false
    }

    @AfterEach
    fun tearDown() {
        System.setOut(originalOut)
        System.setIn(originalIn)
    }

    private fun simulateInput(input: String) {
        val inputStream = ByteArrayInputStream(input.toByteArray())
        System.setIn(inputStream)

        AdminHomeUI(
            viewer = object : Viewer {
                override fun display(message: String?) {
                    println(message)
                }
            },
            reader = ConsoleReader(),
            userRole = UserRole.ADMIN,
            onNavigateToShowAllProjectsUI = { navigateToShowAllProjectsCalled = true },
            onNavigateToCreateProject = { navigateToCreateProjectCalled = true },
            onNavigateToCreateUser = { navigateToCreateUserCalled = true },
            onNavigateToOnBackStack = { navigateToBackCalled = true }
        )
    }

    @Test
    fun `should trigger ShowAllProjects UI when choice is 1`() {
        simulateInput("1\n")
        assertThat(navigateToShowAllProjectsCalled).isTrue()
    }

    @Test
    fun `should trigger CreateProject UI when choice is 2`() {
        simulateInput("2\n")
        assertThat(navigateToCreateProjectCalled).isTrue()
    }

    @Test
    fun `should trigger CreateUser UI when choice is 3`() {
        simulateInput("3\n")
        assertThat(navigateToCreateUserCalled).isTrue()
    }

    @Test
    fun `should trigger BackStack UI when choice is 4`() {
        simulateInput("4\n")
        assertThat(navigateToBackCalled).isTrue()
    }

    @Test
    fun `should trigger BackStack UI when choice is null`() {
        simulateInput("s\n4\n")
        assertThat(navigateToBackCalled).isTrue()
    }

    @Test
    fun `should display error message when choice is invalid`() {
        simulateInput("99\n4\n")
        val output = outContent.toString()
        assertThat(output).contains("Invalid input. Try again.")
        assertThat(navigateToBackCalled).isTrue()
    }
}
