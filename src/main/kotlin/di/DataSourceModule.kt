package org.example.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.data.source.local.contract.LocalAuditLogDataSource
import org.example.data.source.local.contract.LocalAuthenticationDataSource
import org.example.data.source.local.contract.LocalProjectDataSource
import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.data.source.local.csv.CsvAuditLogDataSource
import org.example.data.source.local.csv.CsvAuthenticationDataSource
import org.example.data.source.local.csv.CsvProjectDataSource
import org.example.data.source.local.csv.CsvTaskDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.data.source.remote.contract.RemoteAuditLogDataSource
import org.example.data.source.remote.contract.RemoteAuthenticationDataSource
import org.example.data.source.remote.contract.RemoteProjectDataSource
import org.example.data.source.remote.contract.RemoteTaskDataSource
import org.example.data.source.remote.mongo.*
import org.example.data.utils.Constants
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

val dataSourceModule = module {
    factory<File> { (filePath: String) -> File(filePath) }
    single<CSVReader>(named("tasks-reader")) {
        val file: File = get { parametersOf(Constants.FileNames.TASKS_CSV_FILE_PATH) }
        CSVReader(file)
    }
    single<CSVWriter>(named("tasks-writer")) {
        val file: File = get { parametersOf(Constants.FileNames.TASKS_CSV_FILE_PATH) }
        CSVWriter(file)
    }
    single<CSVReader>(named("users-reader")) {
        val file: File = get { parametersOf(Constants.FileNames.AUTH_CSV_FILE_PATH) }
        CSVReader(file)
    }
    single<CSVWriter>(named("users-writer")) {
        val file: File = get { parametersOf(Constants.FileNames.AUTH_CSV_FILE_PATH) }
        CSVWriter(file)
    }
    single<CSVReader>(named("projects-reader")) {
        val file: File = get { parametersOf(Constants.FileNames.PROJECTS_CSV_FILE_PATH) }
        CSVReader(file)
    }
    single<CSVWriter>(named("projects-writer")) {
        val file: File = get { parametersOf(Constants.FileNames.PROJECTS_CSV_FILE_PATH) }
        CSVWriter(file)
    }
    single<CSVReader>(named("audit-logs-reader")) {
        val file: File = get { parametersOf(Constants.FileNames.AUDIT_LOGS_CSV_FILE_PATH) }
        CSVReader(file)
    }
    single<CSVWriter>(named("audit-logs-writer")) {
        val file: File = get { parametersOf(Constants.FileNames.AUDIT_LOGS_CSV_FILE_PATH) }
        CSVWriter(file)
    }
    single<LocalTaskDataSource> {
        val file = File("tasks.csv")
        CsvTaskDataSource(CSVReader(file), CSVWriter(file))
    }

    single<LocalAuditLogDataSource> {
        val file = File("audit.csv")
        CsvAuditLogDataSource(CSVReader(file), CSVWriter(file))
    }
    single<LocalProjectDataSource> {
        val file = File("projects.csv")
        CsvProjectDataSource(CSVReader(file), CSVWriter(file))
    }
    single<LocalAuthenticationDataSource> {
        val file = File("users.csv")
        CsvAuthenticationDataSource(CSVWriter(file), CSVReader(file))
    }
    single<RemoteProjectDataSource> { MongoProjectDataSource(PlanMateDataBase.projectDoc) }
    single<RemoteAuthenticationDataSource> { MongoAuthenticationDataSource(PlanMateDataBase.userDoc) }
    single<RemoteAuditLogDataSource> { MongoAuditLogDataSource(PlanMateDataBase.auditLogDoc) }
    single<RemoteTaskDataSource> { MongoTaskDataSource(PlanMateDataBase.taskDoc) }


}
