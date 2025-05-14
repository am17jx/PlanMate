package presentation.utils

import org.example.logic.models.AuditLog
import org.example.logic.utils.formattedString

fun AuditLog.toReadableMessage(): String {
    val entityTypeStr = entityType.name.lowercase()
    val formattedTime = createdAt.formattedString()
    return when (actionType) {
        AuditLog.ActionType.CREATE -> "user $userName created $entityTypeStr $entityName at $formattedTime"

        AuditLog.ActionType.DELETE -> "user $userName deleted $entityTypeStr $entityName at $formattedTime"

        AuditLog.ActionType.UPDATE -> {
            if (fieldChange != null) {
                val oldVal = fieldChange.oldValue
                val newVal = fieldChange.newValue

                "user $userName changed $entityTypeStr ${fieldChange.fieldName} " + "from $oldVal to $newVal at $formattedTime"
            } else {
                "user $userName updated $entityTypeStr ($entityName) at $formattedTime"
            }
        }
    }
}
