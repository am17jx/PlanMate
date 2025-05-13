package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import mockdata.createAuditLog
import mockdata.createProject
import mockdata.createState
import mockdata.createUser
import org.example.logic.models.AuditLog
import org.example.logic.models.Project
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.useCase.Validation
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateProjectUseCaseTest {
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var validation: Validation
    private lateinit var projectRepository: ProjectRepository
    private val projectId = Uuid.random()
    private val originalProject = Project(
        id = projectId,
        name = "Original Project",
    )
    private val updatedProject = Project(
        id = projectId,
        name = "Updated Project",
    )

    @BeforeEach
    fun setup() {
        createAuditLogUseCase = mockk(relaxed = true)
        validation = mockk(relaxed = true)
        projectRepository = mockk(relaxed = true)
        updateProjectUseCase = UpdateProjectUseCase(projectRepository, createAuditLogUseCase,validation)
    }
    @Test
    fun `should update project and create audit logs when project has changes`() = runTest {
      
        every { validation.validateInputNotBlankOrThrow(updatedProject.name) } just Runs
        coEvery { projectRepository.getProjectById(projectId) } returns originalProject
        coEvery { projectRepository.updateProject(updatedProject) } returns updatedProject
        coEvery {
            createAuditLogUseCase.logUpdate(
                entityId = any(),
                entityName = any(),
                entityType = any(),
                fieldChange = any()
            )
        } returns AuditLog(
            id = Uuid.random(),
            userId = Uuid.random(),
            userName = "Test User",
            entityId = projectId,
            entityName = updatedProject.name,
            entityType = AuditLog.EntityType.PROJECT,
            actionType = AuditLog.ActionType.UPDATE,
            fieldChange = AuditLog.FieldChange(
                fieldName = "name",
                oldValue = originalProject.name,
                newValue = updatedProject.name
            )
        )

        
        val result = updateProjectUseCase(updatedProject)

        
        assertThat(result).isEqualTo(updatedProject)
        coVerify(exactly = 1) { validation.validateInputNotBlankOrThrow(updatedProject.name) }
        coVerify(exactly = 1) { projectRepository.getProjectById(projectId) }
        coVerify(exactly = 1) { projectRepository.updateProject(updatedProject) }
        coVerify(exactly = 1) {
            createAuditLogUseCase.logUpdate(
                entityId = projectId,
                entityName = updatedProject.name,
                entityType = AuditLog.EntityType.PROJECT,
                fieldChange = match {
                    it.fieldName == "name" &&
                            it.oldValue == originalProject.name &&
                            it.newValue == updatedProject.name
                }
            )
        }
    }

    @Test
    fun `should throw BlankInputException when project name is blank`() = runTest {
      
        val invalidProject = updatedProject.copy(name = "")
        every { validation.validateInputNotBlankOrThrow("") } throws BlankInputException()

      
        assertThrows<BlankInputException> {
            updateProjectUseCase(invalidProject)
        }

        coVerify(exactly = 0) { projectRepository.getProjectById(any()) }
        coVerify(exactly = 0) { projectRepository.updateProject(any()) }
        coVerify(exactly = 0) { createAuditLogUseCase.logUpdate(any(), any(), any(), any()) }
    }

    @Test
    fun `should throw ProjectNotChangedException when project name is empty`() = runTest {
        val updatedProject = createProject(name = "")

        assertThrows<ProjectNotChangedException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throw ProjectNotChangedException when project name has not changed`() = runTest {
       
        val sameProject = originalProject.copy()
        every { validation.validateInputNotBlankOrThrow(sameProject.name) } just Runs
        coEvery { projectRepository.getProjectById(projectId) } returns originalProject

        
        assertThrows<ProjectNotChangedException> {
            updateProjectUseCase(sameProject)
        }

        coVerify(exactly = 1) { projectRepository.getProjectById(projectId) }
        coVerify(exactly = 0) { projectRepository.updateProject(any()) }
        coVerify(exactly = 0) { createAuditLogUseCase.logUpdate(any(), any(), any(), any()) }
    }

    @Test
    fun `should throw ProjectNotFoundException when project is not found in currentOriginalProject`() = runTest {
       
        every { validation.validateInputNotBlankOrThrow(updatedProject.name) } just Runs
        coEvery { projectRepository.getProjectById(projectId) } returns null

        
         assertThrows<ProjectNotFoundException> {
            updateProjectUseCase(updatedProject)
        }

        // Verify that the exception is thrown immediately after trying to get the project
        coVerify(exactly = 1) { validation.validateInputNotBlankOrThrow(updatedProject.name) }
        coVerify(exactly = 1) { projectRepository.getProjectById(projectId) }
        coVerify(exactly = 0) { projectRepository.updateProject(any()) }
        coVerify(exactly = 0) { createAuditLogUseCase.logUpdate(any(), any(), any(), any()) }

    }

    @Test
    fun `should throw NoLoggedInUserException when creating audit logs without logged in user`() = runTest {
        
        every { validation.validateInputNotBlankOrThrow(updatedProject.name) } just Runs
        coEvery { projectRepository.getProjectById(projectId) } returns originalProject
        coEvery { projectRepository.updateProject(updatedProject) } returns updatedProject
        coEvery {
            createAuditLogUseCase.logUpdate(
                entityId = any(),
                entityName = any(),
                entityType = any(),
                fieldChange = any()
            )
        } throws NoLoggedInUserException()

        val exception = assertThrows<NoLoggedInUserException> {
            updateProjectUseCase(updatedProject)
        }

        // Verify that project was updated but audit log creation failed
        coVerify(exactly = 1) { validation.validateInputNotBlankOrThrow(updatedProject.name) }
        coVerify(exactly = 1) { projectRepository.getProjectById(projectId) }
        coVerify(exactly = 1) { projectRepository.updateProject(updatedProject) }
        coVerify(exactly = 1) {
            createAuditLogUseCase.logUpdate(
                entityId = projectId,
                entityName = updatedProject.name,
                entityType = AuditLog.EntityType.PROJECT,
                fieldChange = any()
            )
        }

    }
    @Test
    fun `should throw Exception when creating audit logs failed`() = runTest {
       
        every { validation.validateInputNotBlankOrThrow(updatedProject.name) } just Runs
        coEvery { projectRepository.getProjectById(projectId) } returns originalProject
        coEvery { projectRepository.updateProject(updatedProject) } returns updatedProject
        coEvery {
            createAuditLogUseCase.logUpdate(
                entityId = any(),
                entityName = any(),
                entityType = any(),
                fieldChange = any()
            )
        } throws Exception()

         assertThrows<Exception> {
            updateProjectUseCase(updatedProject)
        }

        // Verify that project was updated but audit log creation failed
        coVerify(exactly = 1) { validation.validateInputNotBlankOrThrow(updatedProject.name) }
        coVerify(exactly = 1) { projectRepository.getProjectById(projectId) }
        coVerify(exactly = 1) { projectRepository.updateProject(updatedProject) }
        coVerify(exactly = 1) {
            createAuditLogUseCase.logUpdate(
                entityId = projectId,
                entityName = updatedProject.name,
                entityType = AuditLog.EntityType.PROJECT,
                fieldChange = any()
            )
        }

    }
    // ... existing code ...

    @Test
    fun `should handle validation failure for project name with special characters`() = runTest {
       
        val invalidProject = updatedProject.copy(name = "Project@#$%")
        every { validation.validateInputNotBlankOrThrow(invalidProject.name) } throws InvalidInputException()

        
        assertThrows<InvalidInputException> {
            updateProjectUseCase(invalidProject)
        }

        coVerify(exactly = 0) { projectRepository.getProjectById(any()) }
        coVerify(exactly = 0) { projectRepository.updateProject(any()) }
        coVerify(exactly = 0) { createAuditLogUseCase.logUpdate(any(), any(), any(), any()) }
    }

    @Test
    fun `should handle repository failure when updating project`() = runTest {
     
        every { validation.validateInputNotBlankOrThrow(updatedProject.name) } just Runs
        coEvery { projectRepository.getProjectById(projectId) } returns originalProject
        coEvery { projectRepository.updateProject(updatedProject) } throws RuntimeException("Database error")

     
        assertThrows<RuntimeException> {
            updateProjectUseCase(updatedProject)
        }

        coVerify(exactly = 1) { validation.validateInputNotBlankOrThrow(updatedProject.name) }
        coVerify(exactly = 1) { projectRepository.getProjectById(projectId) }
        coVerify(exactly = 1) { projectRepository.updateProject(updatedProject) }
        coVerify(exactly = 0) { createAuditLogUseCase.logUpdate(any(), any(), any(), any()) }
    }

    @Test
    fun `should handle audit log creation failure`() = runTest {
    
        every { validation.validateInputNotBlankOrThrow(updatedProject.name) } just Runs
        coEvery { projectRepository.getProjectById(projectId) } returns originalProject
        coEvery { projectRepository.updateProject(updatedProject) } returns updatedProject
        coEvery {
            createAuditLogUseCase.logUpdate(
                entityId = any(),
                entityName = any(),
                entityType = any(),
                fieldChange = any()
            )
        } throws RuntimeException("Audit log creation failed")

      
        val exception = assertThrows<RuntimeException> {
            updateProjectUseCase(updatedProject)
        }
        assert(exception.message == "Audit log creation failed")

        coVerify(exactly = 1) { validation.validateInputNotBlankOrThrow(updatedProject.name) }
        coVerify(exactly = 1) { projectRepository.getProjectById(projectId) }
        coVerify(exactly = 1) { projectRepository.updateProject(updatedProject) }
        coVerify(exactly = 1) {
            createAuditLogUseCase.logUpdate(
                entityId = projectId,
                entityName = updatedProject.name,
                entityType = AuditLog.EntityType.PROJECT,
                fieldChange = match {
                    it.fieldName == "name" &&
                            it.oldValue == originalProject.name &&
                            it.newValue == updatedProject.name
                }
            )
        }
    }


}
//    @Test
//    fun `should throws ProjectNotChangedException when audit updating project log return exception`() = runTest {
//        val updatedProject = createProject(name = "Plan")
//        coEvery { currentUserUseCase() } returns createUser(role = UserRole.ADMIN)
//        coEvery { projectRepository.getProjectById(updatedProject.id) } returns createProject()
//        coEvery { auditLogRepository.createAuditLog(any()) } throws ProjectNotChangedException()
//
//        assertThrows<ProjectNotChangedException> {
//            updateProjectUseCase(updatedProject)
//        }
//    }
//
//    @Test
//    fun `should throw NoLoggedInUserException when no user is logged in`() = runTest {
//        val updatedProject = createProject(name = "PlanMate")
//        coEvery { currentUserUseCase() } throws NoLoggedInUserException()
//
//        assertThrows<NoLoggedInUserException> {
//            updateProjectUseCase(updatedProject)
//        }
//    }
//
//    @Test
//    fun `should throw ProjectNotFoundException when project does not exist`() = runTest {
//        val updatedProject = createProject(name = "Updated")
//        coEvery { currentUserUseCase() } returns createUser(role = UserRole.ADMIN)
//        coEvery { projectRepository.getProjectById(updatedProject.id) } throws ProjectNotFoundException()
//
//        assertThrows<ProjectNotFoundException> {
//            updateProjectUseCase(updatedProject)
//        }
//    }
//
//    @Test
//    fun `should update project successfully when only name is changed`() = runTest {
//        val originalProject = createProject(id = Uuid.random(), "plans mate")
//        val updatedProject = createProject(id = Uuid.random(), "plan mate")
//        val currentUser = createUser(role = UserRole.ADMIN)
//        val auditLog = createAuditLog(Uuid.random(), userId = currentUser.id)
//        coEvery { currentUserUseCase() } returns currentUser
//        coEvery { projectRepository.getProjectById(updatedProject.id) } returns originalProject
//        coEvery { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog
//
//        val result = updateProjectUseCase(updatedProject)
//
//        assertThat(result.name).isEqualTo(updatedProject.name)
//    }
//
//    @Test
//    fun `should update project successfully when a state is updated`() = runTest {
//        val originalProject = createProject(
//            id = Uuid.random(),
//            name = "new",
//        )
//        val updatedProject = createProject(
//            id = Uuid.random(),
//            name = "new"
//        )
//        val currentUser = createUser(role = UserRole.ADMIN)
//        val auditLog = createAuditLog(Uuid.random(), userId = currentUser.id)
//        coEvery { currentUserUseCase() } returns currentUser
//        coEvery { projectRepository.getProjectById(updatedProject.id) } returns originalProject
//        coEvery { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog
//
//        val result = updateProjectUseCase(updatedProject)
//
//        assertThat(result.name).isEqualTo(updatedProject.name)
//        coVerify { auditLogRepository.createAuditLog(any()) }
//    }
//
//    @Test
//    fun `should update project successfully when a state is added`() = runTest {
//        val originalProject = createProject(id = Uuid.random(), name = "new")
//        val updatedProject = createProject(id = Uuid.random(), name = "new")
//        val currentUser = createUser(role = UserRole.ADMIN)
//        val auditLog = createAuditLog(Uuid.random(), userId = currentUser.id)
//        coEvery { currentUserUseCase() } returns currentUser
//        coEvery { projectRepository.getProjectById(updatedProject.id) } returns originalProject
//        coEvery { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog
//
//        val result = updateProjectUseCase(updatedProject)
//
//        assertThat(result.name).isEqualTo(updatedProject.name)
//        coVerify { auditLogRepository.createAuditLog(any()) }
//    }
//
//    @Test
//    fun `should update project successfully when a state is deleted`() = runTest {
//        val originalProject = createProject(
//            id = Uuid.random(),
//            name = "new"
//        )
//        val updatedProject = createProject(
//            id = Uuid.random(),
//            name = "new",
//        )
//
//        val currentUser = createUser(role = UserRole.ADMIN)
//        val auditLog = createAuditLog(Uuid.random(), userId = currentUser.id)
//        coEvery { currentUserUseCase() } returns currentUser
//        coEvery { projectRepository.getProjectById(updatedProject.id) } returns originalProject
//        coEvery { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog
//
//        val result = updateProjectUseCase(updatedProject)
//
//        assertThat(result.name).isEqualTo(updatedProject.name)
//        coVerify { auditLogRepository.createAuditLog(any()) }
//
//    }
