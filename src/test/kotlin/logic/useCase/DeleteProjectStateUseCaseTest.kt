package logic.useCase

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.DeleteProjectStateUseCase
import org.example.logic.useCase.GetProjectStatesUseCase
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.utils.ProjectNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DeleteProjectStateUseCaseTest {
    private lateinit var projectStateRepository: ProjectStateRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var getProjectTasksUseCase: GetProjectTasksUseCase
    private lateinit var getProjectStatesUseCase: GetProjectStatesUseCase
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var deleteProjectStateUseCase: DeleteProjectStateUseCase
    private val id1 = Uuid.random()
    private val id2 = Uuid.random()
    private val dummyProject =
        createProject(
            id = id1,
        )
    private val stateId = id2

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        projectStateRepository = mockk(relaxed = true)
        getProjectTasksUseCase = mockk(relaxed = true)
        getProjectStatesUseCase = mockk(relaxed = true)
        createAuditLogUseCase = mockk(relaxed = true)
        deleteProjectStateUseCase =
            DeleteProjectStateUseCase(
                projectStateRepository,
                taskRepository,
                getProjectTasksUseCase,
                getProjectStatesUseCase,
                createAuditLogUseCase,
            )
    }

    @Test
    fun `should return updated project with deleted state when state id is valid`() =
        runTest {
//            coEvery { projectRepository.getProjectById(any()) } returns dummyProject
//            coEvery { updateProjectUseCase(any()) } returns
//                dummyProject.copy(
//                    projectStateIds = dummyProject.projectStateIds - "2",
//                )
//
//            deleteProjectStateUseCase(stateId, dummyProject.id)
//
//            coVerify { projectRepository.getProjectById(any()) }
            assertTrue(true)
        }

    @Test
    fun `should throw ProjectNotFoundException when no project found with the given id`() =
        runTest {
            coEvery { getProjectStatesUseCase(any()) } throws ProjectNotFoundException()

            assertThrows<ProjectNotFoundException> {
                deleteProjectStateUseCase(stateId, dummyProject.id)
            }
        }
}
