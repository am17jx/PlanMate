package org.example.data.source.local.csv.utils.command

interface Command {
    suspend fun execute()
    suspend fun undo()
}