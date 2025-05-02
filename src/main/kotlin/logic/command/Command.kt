package org.example.logic.command

interface Command {
    fun execute()
    fun undo()
}