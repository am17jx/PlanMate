package data.source

import data.source.local.contract.LocalAuthenticationDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.example.data.utils.mapper.toCsvRow
import org.example.data.utils.mapper.toUsers
import org.example.logic.models.User

class CsvAuthenticationDataSource(
    private val csvWriter: CSVWriter,
    private val csvReader: CSVReader
): LocalAuthenticationDataSource {

    override fun saveUser(user: User) {
        val allUsersRows = csvReader.readLines() + user.toCsvRow()
        csvWriter.writeLines(allUsersRows)
    }

    override fun getAllUsers(): List<User> {
        return csvReader.readLines().toUsers()
    }
}