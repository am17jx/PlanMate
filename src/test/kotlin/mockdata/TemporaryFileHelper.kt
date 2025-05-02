package mockdata

import java.io.File

fun withTempFile(fileName: String, block: (File) -> Unit) {
    val file = File(fileName)
    try {
        block(file)
    } finally {
        if (!file.delete()) {
            println("Warning: Failed to delete temporary file: ${file.absolutePath}")
        }
    }
}