package di

import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.repository.TaskRepositoryImpl
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.*
import org.example.logic.useCase.createProject.CreateProjectUseCase
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.*

val repositoryModule =
    module {

        // ====== Authentication Repository ======
        single<AuthenticationRepository> {
            object : AuthenticationRepository {
                private val users =
                    mutableListOf(
                        User(
                            id = UUID.randomUUID().toString(),
                            username = "sara",
                            password = "fcea920f7412b5da7be0cf42b8c93759", // "1234567" hashed with MD5
                            role = UserRole.USER,
                        ),
                        User(
                            id = UUID.randomUUID().toString(),
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
                            id = UUID.randomUUID().toString(),
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
                    entityType: org.example.logic.models.AuditLogEntityType,
                ): List<org.example.logic.models.AuditLog> = logs.filter { it.entityId == entityId && it.entityType == entityType }

                override fun getEntityLogByLogId(auditLogId: String): org.example.logic.models.AuditLog? = null
            }
        }

        singleOf(::TaskRepositoryImpl) { bind<TaskRepository>() }
        singleOf(::ProjectRepositoryImpl) { bind<ProjectRepository>() }
    }
