package di

import data.source.CsvAuthenticationDataSource
import data.source.local.contract.LocalAuthenticationDataSource
import org.koin.dsl.module
import java.io.File

val dataSourceModule =
    module {
        single<LocalAuthenticationDataSource> { CsvAuthenticationDataSource(File("users.csv")) }
    }