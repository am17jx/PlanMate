package org.example.data.utils

object Constants {
    object ProjectParsingKeys {
        const val ID_INDEX = 0
        const val NAME_INDEX = 1
        const val STATES_INDEX = 2
        const val AUDIT_LOGS_IDS_INDEX = 3
    }

    object FileNames {
        const val TASKS_CSV_FILE_PATH = "tasks.csv"
        const val AUTH_CSV_FILE_PATH = "users.csv"
        const val PROJECTS_CSV_FILE_PATH = "projects.csv"
        const val AUDIT_LOGS_CSV_FILE_PATH = "audit-logs.csv"
    }

    object CollectionNames {
        const val TASKS_DOCUMENTATION = "Task"
        const val PROJECTS_DOCUMENTATION = "Project"
        const val USERS_DOCUMENTATION = "User"
        const val AUDIT_LOGS_DOCUMENTATION = "AuditLog"
        const val STATE_DOCUMENTATION = "State"
    }

    const val DATABASE_NAME: String = "PlanMateDatabase"
    const val MONGODB_URI:String="MONGODB_URI"
    const val ID:String = "id"
    const val PROJECT_ID:String = "projectId"
    const val ENTITY_ID = "entityId"
    const val ENTITY_TYPE = "entityType"

    const val STATE_ID_FIELD = "stateId"

    const val USERNAME_FIELD = "username"
    const val AUTH_TYPE_FIELD = "authMethod.type"
    const val PASSWORD_FIELD = "authMethod.password"
}
