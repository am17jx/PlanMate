package org.example.di

import data.source.CsvAuthenticationDataSource
import data.source.local.contract.LocalAuthenticationDataSource
import org.example.data.source.CsvAuditLogDataSource
import org.example.data.source.local.CsvProjectDataSource
import org.example.data.source.local.CsvTaskDataSource
import org.example.data.source.local.contract.LocalProjectDataSource
import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.data.source.local.cotract.LocalAuditLogDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.example.data.utils.Constants
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.bind
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import java.io.File

val dataSourceModule = module {
    single<File> { (filePath : String) -> File(filePath) }
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
        CsvTaskDataSource(get(qualifier = named("tasks-reader")), get(qualifier = named("tasks-writer")))
    }

    single<LocalAuditLogDataSource> {
        CsvAuditLogDataSource(get(qualifier = named("audit-logs-reader")), get(qualifier = named("audit-logs-writer")))
    }
    single<LocalProjectDataSource> {
        CsvProjectDataSource(get(qualifier = named("projects-reader")), get(qualifier = named("projects-writer")))
    }
    single<LocalAuthenticationDataSource> {
        CsvAuthenticationDataSource(get(qualifier = named("users-writer")), get(qualifier = named("users-reader")))

    }
}