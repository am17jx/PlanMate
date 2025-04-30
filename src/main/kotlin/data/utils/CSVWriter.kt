package org.example.data.utils

import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException

class CSVWriter(
    private val file: File,
) {
    fun writeLines(lines: List<String>, append: Boolean = false) {
        TODO("Not yet implemented")
    }
    companion object{
        const val CANNOT_WRITE_TO_FILE_ERROR_MESSAGE = "Cannot write to CSV file"
        const val DIRECTORY_INSTEAD_OF_FILE_ERROR_MESSAGE = "Expected a file but got a directory"
        const val EMPTY_LINES_ERROR_MESSAGE = "Cannot write empty lines to CSV file"
    }
}