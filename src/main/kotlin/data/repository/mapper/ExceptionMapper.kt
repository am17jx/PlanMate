package org.example.data.repository.mapper

import com.mongodb.*
import org.example.logic.utils.DataBaseException
import org.example.logic.utils.UnauthorizedAccessException


suspend fun <T> mapExceptionsToDomainException(customException: Exception, operation: suspend () -> T): T {

    try {
        return operation()
    } catch (exception: Exception) {
        throw when (exception) {
            is MongoSocketOpenException,
            is MongoTimeoutException,
            is MongoExecutionTimeoutException,
            is MongoInterruptedException,
            is MongoNodeIsRecoveringException,
                -> DataBaseException()

            is MongoSecurityException -> UnauthorizedAccessException()

            else -> customException
        }
    }


}
