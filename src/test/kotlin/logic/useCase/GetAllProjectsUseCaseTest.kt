package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.GetAllProjectsUseCase
import org.example.logic.utils.NoProjectsFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetAllProjectsUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private val projects =
        listOf(
            Project(
                id = Uuid.random(),
                name = "Spacecraft Work",
            ),
            Project(
                id = Uuid.random(),
                name = "Mars Rover Development",
            ),
            Project(
                id = Uuid.random(),
                name = "Satellite Deployment",
            ),
            Project(
                id = Uuid.random(),
                name = "Empty Project",
            ),
            Project(
                id = Uuid.random(),
                name = "Lunar Base Planning",
            ),
        )

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        getAllProjectsUseCase = GetAllProjectsUseCase(projectRepository)
    }

    @Test
    fun `should return all projects when found projects at file`() =
        runTest {
            coEvery { projectRepository.getAllProjects() } returns projects

            assertThat(getAllProjectsUseCase()).isEqualTo(projects)
        }


}
