package org.example.data.source.remote.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import data.source.remote.mongo.utils.AuthenticationMethodDtoCodec
import io.github.cdimascio.dotenv.dotenv
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
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

    private val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
        CodecRegistries.fromCodecs(AuthenticationMethodDtoCodec()),
        MongoClientSettings.getDefaultCodecRegistry()
    )

    private val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(uri))
        .codecRegistry(codecRegistry)
        .build()

    private val client = MongoClient.create(settings)
    private val database = client.getDatabase(Constants.DATABASE_NAME)

    val projectDoc = database.getCollection<ProjectDTO>(PROJECTS_DOCUMENTATION)
    val taskDoc = database.getCollection<TaskDTO>(TASKS_DOCUMENTATION)
    val userDoc = database.getCollection<UserDTO>(USERS_DOCUMENTATION)
    val auditLogDoc = database.getCollection<AuditLogDTO>(AUDIT_LOGS_DOCUMENTATION)
    val stateDoc = database.getCollection<ProjectStateDTO>(STATE_DOCUMENTATION)
}
