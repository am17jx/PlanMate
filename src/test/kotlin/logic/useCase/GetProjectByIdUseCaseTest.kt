package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidInputException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetProjectByIdUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private val ids = List(6) { Uuid.random() }
    private val project = createProject(
        id = ids[1],
        name = "spacecraft work",
    )

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        getProjectByIdUseCase = GetProjectByIdUseCase(projectRepository)
    }

    @Test
    fun `should return project when pass valid id have char and number only`() = runTest {
        val projectId = ids[4]
        coEvery { projectRepository.getProjectById(projectId) } returns project
        val result = getProjectByIdUseCase(projectId)

        assertThat(result).isEqualTo(project)
    }

    @Test
    fun `should throw BlankInputException when pass blank id`() = runTest {
        val projectId = ids[4]
        coEvery { projectRepository.getProjectById(projectId) } returns null

        assertThrows<BlankInputException> {
            getProjectByIdUseCase(projectId)
        }
    }

    @Test
    fun `should throw InvalidInputException when pass invalid id have special chars`() = runTest {
        val projectId = ids[3]
        coEvery { projectRepository.getProjectById(projectId) } returns null
        assertThrows<InvalidInputException> {
            getProjectByIdUseCase(projectId)
        }
    }
}
