package org.example.di

import org.example.data.source.local.CsvTaskDataSource
import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataSourceModule = module {
    singleOf(::CSVReader)
    singleOf(::CSVWriter)
    singleOf(::CsvTaskDataSource) { bind<LocalTaskDataSource>()}
}