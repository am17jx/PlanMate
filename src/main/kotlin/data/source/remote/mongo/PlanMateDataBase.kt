package org.example.data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import org.example.data.source.remote.models.*
import org.example.data.utils.Constants
import org.example.data.utils.Constants.CollectionNames.AUDIT_LOGS_DOCUMENTATION
import org.example.data.utils.Constants.CollectionNames.PROJECTS_DOCUMENTATION
import org.example.data.utils.Constants.CollectionNames.STATE_DOCUMENTATION
import org.example.data.utils.Constants.CollectionNames.TASKS_DOCUMENTATION
import org.example.data.utils.Constants.CollectionNames.USERS_DOCUMENTATION
import org.example.data.utils.Constants.MONGODB_URI
import org.example.logic.utils.DataBaseException


object PlanMateDataBase {

    private val uri: String = dotenv()[MONGODB_URI] ?: throw DataBaseException()
    private val client = MongoClient.create(connectionString = uri)
    private val database = client.getDatabase(databaseName = Constants.DATABASE_NAME)


    val projectDoc = database.getCollection<ProjectDTO>(collectionName = PROJECTS_DOCUMENTATION)
    val taskDoc = database.getCollection<TaskDTO>(collectionName = TASKS_DOCUMENTATION)
    val userDoc = database.getCollection<UserDTO>(collectionName = USERS_DOCUMENTATION)
    val auditLogDoc = database.getCollection<AuditLogDTO>(collectionName = AUDIT_LOGS_DOCUMENTATION)
    val stateDoc = database.getCollection<StateDTO>(collectionName = STATE_DOCUMENTATION)

}
