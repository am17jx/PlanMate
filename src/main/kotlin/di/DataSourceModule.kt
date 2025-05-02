package org.example.di

import data.source.CsvAuthenticationDataSource
import data.source.local.contract.LocalAuthenticationDataSource
import org.example.data.source.CsvAuditLogDataSource
import org.example.data.source.local.cotract.LocalAuditLogDataSource
import org.example.data.source.local.CsvTaskDataSource
import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.bind
import java.io.File

val dataSourceModule = module {
    singleOf(::CSVReader)
    singleOf(::CSVWriter)
    singleOf(::CsvTaskDataSource) { bind<LocalTaskDataSource>()}
    singleOf(::CsvAuditLogDataSource) { bind<LocalAuditLogDataSource>()}
    single<LocalAuthenticationDataSource> { CsvAuthenticationDataSource(File("users.csv")) }
}