package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.repository.sources.remote.RemoteProjectDataSource
import org.example.data.source.remote.RoleValidationInterceptor
import org.example.logic.models.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProjectRepositoryImplTest {
    private lateinit var mockRemoteDataSource: RemoteProjectDataSource
    private lateinit var repository: ProjectRepositoryImpl
    private lateinit var testProjects: List<Project>
    private lateinit var roleValidationInterceptor: RoleValidationInterceptor
    private val id1 = Uuid.random()
    private val id2 = Uuid.random()
    private val id3 = Uuid.random()

    @BeforeEach
    fun setUp() {
        mockRemoteDataSource = mockk(relaxed = true)
        roleValidationInterceptor = mockk(relaxed = true)

        testProjects =
            listOf(
                Project(
                    id = id1,
                    name = "Project 1",
                ),
                Project(
                    id = id2,
                    name = "Project 2",
                ),
            )
        coEvery { mockRemoteDataSource.getAllProjects() } returns testProjects
        repository = ProjectRepositoryImpl(mockRemoteDataSource, roleValidationInterceptor)
    }

    @Test
    fun `createProject should delegates to localDataSource and returns result`() =
        runTest {
            val newProject =
                Project(
                    id = id3,
                    name = "Project 3",
                )
            coEvery { roleValidationInterceptor.validateRole<Project>(any(), any()) } returns newProject

            val result = repository.createProject(newProject)

            coVerify(exactly = 1) { roleValidationInterceptor.validateRole<Project>(any(), any()) }
            assertThat(result).isEqualTo(newProject)
        }

    @Test
    fun `updateProject should delegates to localDataSource and returns result`() =
        runTest {
            val updatedProject =
                Project(
                    id = id1,
                    name = "Updated Project 1",
                )
            coEvery { roleValidationInterceptor.validateRole<Project>(any(), any()) } returns updatedProject

            val result = repository.updateProject(updatedProject)

            coVerify(exactly = 1) { roleValidationInterceptor.validateRole<Project>(any(), any()) }
            assertThat(result).isEqualTo(updatedProject)
        }

    @Test
    fun `getAllProjects should delegates to localDataSource and returns result`() =
        runTest {
            val result = repository.getAllProjects()

            coVerify(exactly = 1) { mockRemoteDataSource.getAllProjects() }
            assertThat(result).isEqualTo(testProjects)
            assertThat(result).hasSize(2)
        }

    @Test
    fun `deleteProject should delegates to localDataSource`() =
        runTest {
            val projectIdToDelete = id1
            coEvery { mockRemoteDataSource.deleteProject(any()) } just runs
            coEvery { roleValidationInterceptor.validateRole<Project>(any(), any()) } returns
                Project(
                    id = id1,
                    name = "Project 1",
                )

            repository.deleteProject(projectIdToDelete)

            coVerify(exactly = 1) { roleValidationInterceptor.validateRole<Project>(any(), any()) }
        }

    @Test
    fun `getProjectById should returns project when found`() =
        runTest {
            val projectId = id1
            coEvery { mockRemoteDataSource.getProjectById(any()) } returns
                Project(
                    id = id1,
                    name = "Project 1",
                )

            val result = repository.getProjectById(projectId)

            coVerify(exactly = 1) { mockRemoteDataSource.getProjectById(projectId) }
            assertThat(result).isNotNull()
            assertThat(result?.id).isEqualTo(id1)
            assertThat(result?.name).isEqualTo("Project 1")
        }
}
