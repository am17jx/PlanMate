package di

import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.*
import org.example.logic.useCase.CreateProjectUseCase
import org.koin.dsl.module
import java.util.*

val repositoryModule = module {

    // ====== Authentication Repository ======
    single<AuthenticationRepository> {
        object : AuthenticationRepository {

            private val users = mutableListOf(
                User(
                    id = UUID.randomUUID().toString(),
                    username = "sara",
                    password = "fcea920f7412b5da7be0cf42b8c93759", // "1234567" hashed with MD5
                    role = UserRole.USER
                ),
                User(
                    id = UUID.randomUUID().toString(),
                    username = "admin",
                    password = "21232f297a57a5a743894a0e4a801fc3", // "admin" hashed with MD5
                    role = UserRole.ADMIN
                )
            )

            private var currentUser: User? = null

            override fun getCurrentUser(): User? = currentUser

            override fun createMate(username: String, hashedPassword: String): User {
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    username = username,
                    password = hashedPassword,
                    role = UserRole.USER
                )
                users.add(newUser)
                return newUser
            }

            override fun login(username: String, hashedPassword: String): User {
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
            private val projects = mutableListOf<org.example.logic.models.Project>()

            override fun createProject(project: org.example.logic.models.Project): org.example.logic.models.Project? {
                projects.add(project)
                return project
            }

            override fun getAllProjects(): List<org.example.logic.models.Project> {
                return projects
            }

            override fun getProjectById(id: String): org.example.logic.models.Project? {
                return projects.find { it.id == id }
            }

            override fun updateProject(updatedProject: org.example.logic.models.Project): org.example.logic.models.Project {
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


    // ====== Audit Log Repository (Dummy Implementation) ======
    single<AuditLogRepository> {
        object : AuditLogRepository {
            private val logs = mutableListOf<org.example.logic.models.AuditLog>()

            override fun createAuditLog(log: org.example.logic.models.AuditLog): org.example.logic.models.AuditLog {
                logs.add(log)
                return log
            }

            override fun deleteAuditLog(logId: String) {
                logs.removeIf { it.id == logId }
            }

            override fun getEntityLogs(
                entityId: String,
                entityType: org.example.logic.models.AuditLogEntityType
            ): List<org.example.logic.models.AuditLog> {
                return logs.filter { it.entityId == entityId && it.entityType == entityType }
            }
            override fun getEntityLogByLogId(
                auditLogId: String,
            ): org.example.logic.models.AuditLog? {
               return null
            }
        }
    }


    // ====== CreateProjectUseCase ======
    single {
        CreateProjectUseCase(
            projectRepository = get(),
            auditLogRepository = get(),
            authenticationRepository = get()
        )
    }


}
