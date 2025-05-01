package di

import org.example.data.repository.TaskRepositoryImpl
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType
import org.example.logic.models.Project
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.CreateProjectUseCase
import org.example.logic.utils.getCroppedId
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
val repositoryModule =
    module {

        // ====== Authentication Repository ======
        single<AuthenticationRepository> {
            object : AuthenticationRepository {
                private val users =
                    mutableListOf(
                        User(
                            id = Uuid.random().getCroppedId(),
                            username = "sara",
                            password = "fcea920f7412b5da7be0cf42b8c93759", // "1234567" hashed with MD5
                            role = UserRole.USER,
                        ),
                        User(
                            id = Uuid.random().getCroppedId(),
                            username = "admin",
                            password = "21232f297a57a5a743894a0e4a801fc3", // "admin" hashed with MD5
                            role = UserRole.ADMIN,
                        ),
                    )

                private var currentUser: User? = null

                override fun getCurrentUser(): User? = currentUser

                override fun createMate(
                    username: String,
                    hashedPassword: String,
                ): User {
                    val newUser =
                        User(
                            id = Uuid.random().getCroppedId(),
                            username = username,
                            password = hashedPassword,
                            role = UserRole.USER,
                        )
                    users.add(newUser)
                    return newUser
                }

                override fun login(
                    username: String,
                    hashedPassword: String,
                ): User {
                    val user = users.first { it.username == username && it.password == hashedPassword }
                    currentUser = user
                    return user
                }

                override fun getAllUsers(): List<User> = users
            }
        }

        // ====== Project Repository (Dummy Implementation) ======
        single<ProjectRepository> {
            object : ProjectRepository {
                private val projects = mutableListOf<Project>()

                override fun createProject(project: Project): Project? {
                    projects.add(project)
                    return project
                }

                override fun getAllProjects(): List<Project> = projects

                override fun getProjectById(id: String): Project? = projects.find { it.id == id }

                override fun updateProject(updatedProject: Project): Project {
                    val index = projects.indexOfFirst { it.id == updatedProject.id }
                    if (index != -1) {
                        projects[index] = updatedProject
                        return updatedProject
                    } else {
                        throw IllegalArgumentException("Project not found")
                    }
                }

                override fun deleteProject(projectId: String) {
                    projects.removeIf { it.id == projectId }
                }
            }
        }

        singleOf(::TaskRepositoryImpl) { bind<TaskRepository>() }

        // ====== Audit Log Repository (Dummy Implementation) ======
        single<AuditLogRepository> {
            object : AuditLogRepository {
                private val logs = mutableListOf<AuditLog>()

                override fun createAuditLog(log: AuditLog): AuditLog {
                    logs.add(log)
                    return log
                }

                override fun deleteAuditLog(logId: String) {
                    logs.removeIf { it.id == logId }
                }

                override fun getEntityLogs(
                    entityId: String,
                    entityType: AuditLogEntityType,
                ): List<AuditLog> = logs.filter { it.entityId == entityId && it.entityType == entityType }

                override fun getEntityLogByLogId(auditLogId: String): AuditLog? = null
            }
        }

        // ====== CreateProjectUseCase ======
        single {
            CreateProjectUseCase(
                projectRepository = get(),
                auditLogRepository = get(),
                authenticationRepository = get(),
            )
        }
    }
