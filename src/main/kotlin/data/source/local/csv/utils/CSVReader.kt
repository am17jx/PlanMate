package org.example.data.source.local.csv.utils

import java.io.File
import java.io.IOException

class CSVReader(
    private val file: File,
) {

    init {
        if (!file.exists()) {
            val parentDir = file.parentFile
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }
            file.createNewFile()
        }
        if (!file.canRead()) {
            throw IOException(CANNOT_READ_FILE_ERROR_MESSAGE)
        }
        if (file.isDirectory()) {
            throw IOException(DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE)
        }
    }

    fun readLines(): List<String> {
        return file.bufferedReader().readLines()
    }

    companion object {
        const val FILE_NOT_FOUND_ERROR_MESSAGE = "CSV file not found"
        const val CANNOT_READ_FILE_ERROR_MESSAGE = "Cannot read from CSV file"
        const val DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE = "Expected a file but got a directory"
    }
}
