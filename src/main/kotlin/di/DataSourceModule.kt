package org.example.di

import data.source.CsvAuthenticationDataSource
import data.source.local.contract.LocalAuthenticationDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.koin.dsl.module
import java.io.File

val dataSourceModule = module {
    single<LocalAuthenticationDataSource> {
        val file = File("users.csv")
        CsvAuthenticationDataSource(CSVWriter(file), CSVReader(file))
    }
}