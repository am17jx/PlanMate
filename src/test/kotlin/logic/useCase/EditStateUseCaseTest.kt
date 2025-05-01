package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mockdata.createProject
import mockdata.createUser
import org.example.logic.models.State
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.EditStateUseCase
import org.example.logic.useCase.UpdateProjectUseCase
import org.example.logic.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EditStateUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var editStateUseCase: EditStateUseCase
    private val dummyProject =
        createProject(
            id = "1",
            states =
                listOf(
                    State(id = "2", title = "StateTest"),
                    State(id = "3", title = "StateTest2"),
                    State(id = "4", title = "StateTest3"),
                ),
            auditLogsIds = listOf("5", "6", "7"),
        )
    private val stateId = "2"
    private val newTitle = "New State Title"

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        authenticationRepository = mockk()
        updateProjectUseCase = mockk()
        editStateUseCase = EditStateUseCase(projectRepository, authenticationRepository, updateProjectUseCase)
    }

    @Test
    fun `should return updated project with deleted state when state id is valid and user is admin`() {
        val updatedStates =
            listOf(
                State(id = "2", title = newTitle),
                State(id = "3", title = "StateTest2"),
                State(id = "4", title = "StateTest3"),
            )
        every { authenticationRepository.getCurrentUser() } returns createUser()
        every { projectRepository.getProjectById(any()) } returns dummyProject
        every { updateProjectUseCase(any()) } returns
            dummyProject.copy(states = updatedStates)

        val updatedProject = editStateUseCase(newTitle, stateId, dummyProject.id)

        verify { projectRepository.getProjectById(any()) }
        verify { authenticationRepository.getCurrentUser() }
        assertThat(updatedProject.states).hasSize(3)
        assertThat(updatedProject.states.find { it.id == stateId }?.title).isEqualTo(newTitle)
    }

    @Test
    fun `should throw BlankInputException when new state name is blank`() {
        val blankStateName = ""
        every { authenticationRepository.getCurrentUser() } returns createUser()

        assertThrows<BlankInputException> {
            editStateUseCase(blankStateName, stateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw BlankInputException when state id is blank`() {
        val blankStateId = ""
        every { authenticationRepository.getCurrentUser() } returns createUser()

        assertThrows<BlankInputException> {
            editStateUseCase(newTitle, blankStateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw BlankInputException when project id is blank`() {
        val blankProjectId = ""
        every { authenticationRepository.getCurrentUser() } returns createUser()

        assertThrows<BlankInputException> {
            editStateUseCase(newTitle, stateId, blankProjectId)
        }
    }

    @Test
    fun `should throw UnauthorizedException when user is not an admin`() {
        every { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.USER)

        assertThrows<UnauthorizedException> {
            editStateUseCase(newTitle, stateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw NoLoggedInUserException when current user is null`() {
        every { authenticationRepository.getCurrentUser() } returns null

        assertThrows<NoLoggedInUserException> {
            editStateUseCase(newTitle, stateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() {
        every { authenticationRepository.getCurrentUser() } returns createUser()
        every { projectRepository.getProjectById(any()) } returns null

        assertThrows<ProjectNotFoundException> {
            editStateUseCase(newTitle, stateId, dummyProject.id)
        }
    }

    @Test
    fun `should throw StateNotFoundException when no state found with the given id`() {
        every { authenticationRepository.getCurrentUser() } returns createUser()
        every { projectRepository.getProjectById(any()) } returns createProject()

        assertThrows<StateNotFoundException> {
            editStateUseCase(newTitle, "5", dummyProject.id)
        }
    }
}
