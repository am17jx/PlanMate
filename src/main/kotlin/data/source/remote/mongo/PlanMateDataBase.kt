package org.example.data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import org.example.data.models.AuditLogDTO
import org.example.data.models.ProjectDTO
import org.example.data.models.TaskDTO
import org.example.data.models.UserDTO
import org.example.data.utils.Constants
import org.example.data.utils.Constants.CollectionNames.AUDIT_LOGS_DOCUMENTATION
import org.example.data.utils.Constants.CollectionNames.PROJECTS_DOCUMENTATION
import org.example.data.utils.Constants.CollectionNames.TASKS_DOCUMENTATION
import org.example.data.utils.Constants.CollectionNames.USERS_DOCUMENTATION
import org.example.data.utils.Constants.MONGODB_URI
import org.example.logic.models.AuditLog
import org.example.logic.utils.DataBaseUriNoFoundException


object PlanMateDataBase {

    private val uri: String = dotenv()[MONGODB_URI]?:throw DataBaseUriNoFoundException("Data base uri not found")
    private val client = MongoClient.create(connectionString =  uri)
    private val database = client.getDatabase(databaseName = Constants.DATABASE_NAME)


    val projectDoc = database.getCollection<ProjectDTO>(collectionName = PROJECTS_DOCUMENTATION)
    val taskDoc = database.getCollection<TaskDTO>(collectionName = TASKS_DOCUMENTATION)
    val userDoc = database.getCollection<UserDTO>(collectionName = USERS_DOCUMENTATION)
    val auditLogDoc = database.getCollection<AuditLogDTO>(collectionName = AUDIT_LOGS_DOCUMENTATION)

}
