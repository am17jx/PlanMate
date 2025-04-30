package org.example.data.utils

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class CSVWriter(
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
        if (!file.canWrite()) {
            throw IOException(CANNOT_WRITE_TO_FILE_ERROR_MESSAGE)
        }
        if (file.isDirectory()) {
            throw IOException(DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE)
        }

    }

    fun writeLines(lines: List<String>, append: Boolean = false) {
        if (lines.isEmpty()) throw IllegalArgumentException(EMPTY_LINES_ERROR_MESSAGE)
        BufferedWriter(FileWriter(file, append)).use { writer ->
            lines.forEachIndexed { index, line ->
                writer.write(line)
                if (index != lines.size - 1) writer.newLine()
            }
        }
    }

    companion object {
        const val FILE_NOT_FOUND_ERROR_MESSAGE = "CSV file not found"
        const val CANNOT_WRITE_TO_FILE_ERROR_MESSAGE = "Cannot write to CSV file"
        const val DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE = "Expected a file but got a directory"
        const val EMPTY_LINES_ERROR_MESSAGE = "Cannot write empty lines to CSV file"
    }
}