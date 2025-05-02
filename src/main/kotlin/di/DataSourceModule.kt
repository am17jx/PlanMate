package org.example.di

import data.source.CsvAuthenticationDataSource
import data.source.local.contract.LocalAuthenticationDataSource
import org.example.data.source.CsvAuditLogDataSource
import org.example.data.source.local.CsvProjectDataSource
import org.example.data.source.local.CsvTaskDataSource
import org.example.data.source.local.contract.LocalProjectDataSource
import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.data.source.local.cotract.LocalAuditLogDataSource
import data.source.CsvAuthenticationDataSource
import data.source.local.contract.LocalAuthenticationDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.bind
import java.io.File

val dataSourceModule = module {
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
}