package org.example.logic.command

interface Command {
    suspend fun execute()
    suspend fun undo()
}