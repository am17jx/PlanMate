package presentation.utils

import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class TablePrinter(
    private val viewer: Viewer,
    private val reader: Reader
) {
    fun printTable(
        headers: List<String>,
        columnValues: List<List<String>>
    ) {
        val columnWidths = calculateColumnsWidth(headers, columnValues)
        printSeparatorLine(columnWidths)
        printHeaderLine(columnWidths, headers)
        printSeparatorLine(columnWidths)
        val rowsCount = headers.maxOf { it.length }
        for (rowIndex in 0 until rowsCount) {
            val rowValues = columnValues.map { column ->
                column.getOrElse(rowIndex) { "" }
            }
            printRowLine(columnWidths, rowValues)
        }
        printSeparatorLine(columnWidths)
    }


    private fun printSeparatorLine(
        columnWidths: List<Int>
    ) {
        val separatorLine = columnWidths.joinToString("") { width ->
            "|-" + "-".repeat(width) + "-"
        } + "|"

        viewer.display(separatorLine)
    }

    private fun printHeaderLine(columnWidths: List<Int>, headers: List<String>) {
        val headerLine = columnWidths.zip(headers).joinToString("") { (width, header) ->
            "| " + header.padEnd(width) + " "
        } + "|"

        viewer.display(headerLine)
    }

    private fun printRowLine(columnWidths: List<Int>, rowValues: List<String>) {
        val rowLine = columnWidths.zip(rowValues).joinToString("") { (width, value) ->
            "| " + value.padEnd(width) + " "
        } + "|"

        viewer.display(rowLine)
    }

    private fun calculateColumnsWidth(headers: List<String>, columnValues: List<List<String>>): List<Int> {
        return headers.zip(columnValues).map { (header, values) ->
            maxOf(header.length, values.maxOfOrNull { it.length } ?: 0)
        }
    }
}