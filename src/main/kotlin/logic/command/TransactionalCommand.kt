package org.example.logic.command

import org.example.logic.utils.ProjectNotChangedException

class TransactionalCommand(private val commands: List<Command>,private val exception:Exception=Exception()) : Command {

    private val executedCommands = mutableListOf<Command>()

    override fun execute() {
        for (command in commands) {
            try {
                command.execute()
                executedCommands.add(command)
            } catch (e: Exception) {
                undo()
                throw exception

            }
        }
    }

    override fun undo() {
        for (command in executedCommands.asReversed()) {
            command.undo()
        }
        executedCommands.clear()
    }
}