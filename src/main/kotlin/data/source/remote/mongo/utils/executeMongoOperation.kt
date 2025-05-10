package org.example.data.source.remote.mongo.utils

import com.mongodb.*

suspend fun <T> executeMongoOperation(operation: suspend () -> T): T {
    return try {
        operation()
    } catch (mongoCommandException: MongoCommandException) {
        throw mongoCommandException
    } catch (mongoSecurityException: MongoSecurityException) {
        throw mongoSecurityException
    } catch (mongoSocketOpenException: MongoSocketOpenException) {
        throw mongoSocketOpenException
    } catch (mongoTimeoutException: MongoTimeoutException) {
        throw mongoTimeoutException
    } catch (mongoWriteException: MongoWriteException) {
        throw mongoWriteException
    } catch (mongoClientException: MongoClientException) {
        throw mongoClientException
    } catch (mongoBulkWriteException: MongoBulkWriteException) {
        throw mongoBulkWriteException
    } catch (mongoInterruptedException: MongoInterruptedException) {
        throw mongoInterruptedException
    } catch (mongoExecutionTimeoutException: MongoExecutionTimeoutException) {
        throw mongoExecutionTimeoutException
    } catch (mongoNodeIsRecoveringException: MongoNodeIsRecoveringException) {
        throw mongoNodeIsRecoveringException
    } catch (mongoNotPrimaryException: MongoNotPrimaryException) {
        throw mongoNotPrimaryException
    } catch (unexpectedException: Exception) {
        throw unexpectedException
    }
}