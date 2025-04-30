package org.example.data.utils

import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class CSVReader(
    private val file: File,
) {

    init {
        if (!file.exists()) {
            throw FileNotFoundException(FILE_NOT_FOUND_ERROR_MESSAGE)
        }
        if (!file.canRead()) {
            throw IOException(CANNOT_READ_FILE_ERROR_MESSAGE)
        }
        if (file.isDirectory()) {
            throw IOException(DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE)
        }
    }

    fun readLines(): List<String> {
        val result = mutableListOf<String>()
        file.bufferedReader().use { reader ->
            var line = ""
            while (reader.readLine().also { line = it } != null) {
                result.add(line)
            }
        }
        return result
    }

    companion object {
        const val FILE_NOT_FOUND_ERROR_MESSAGE = "CSV file not found"
        const val CANNOT_READ_FILE_ERROR_MESSAGE = "Cannot read from CSV file"
        const val DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE = "Expected a file but got a directory"
    }
}
