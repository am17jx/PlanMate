package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.AuditLog
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.DeleteProjectUseCase
import org.example.logic.useCase.GetProjectByIdUseCase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DeleteProjectUseCaseTest {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var deleteProjectUseCase: DeleteProjectUseCase

    private val testProject = Project(
        id = Uuid.random(),
        name = "Test Project"
    )

    @BeforeTest
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        getProjectByIdUseCase = mockk()
        createAuditLogUseCase = mockk(relaxed = true)

        deleteProjectUseCase = DeleteProjectUseCase(
            projectRepository = projectRepository,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createAuditLogUseCase = createAuditLogUseCase
        )

        coEvery { getProjectByIdUseCase(testProject.id) } returns testProject
    }

    @Test
    fun `should delete project and create audit log`() = runTest {
        deleteProjectUseCase(testProject.id)

        coVerify { projectRepository.deleteProject(testProject.id) }

        coVerify {
            createAuditLogUseCase.logDeletion(
                entityType = AuditLog.EntityType.PROJECT,
                entityId = testProject.id,
                entityName = testProject.name
            )
        }
    }
}
