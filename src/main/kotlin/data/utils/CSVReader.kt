package org.example.data.utils

import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class CSVReader(
    private val file: File,
) {
    fun readLines(): List<String> {
        TODO("not yet implemented")
    }
    companion object{
        const val FILE_NOT_FOUND_ERROR_MESSAGE = "CSV file not found"
        const val CANNOT_READ_FILE_ERROR_MESSAGE = "Cannot read from CSV file"
        const val DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE = "Expected a file but got a directory"
    }
}