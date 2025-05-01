package org.example.di

import org.example.data.source.CsvAuditLogDataSource
import org.example.data.source.local.cotract.LocalAuditLogDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.core.module.dsl.bind

val dataSourceModule = module {
    singleOf(::CSVReader)
    singleOf(::CSVWriter)
    singleOf(::CsvAuditLogDataSource) { bind<LocalAuditLogDataSource>()}
}
