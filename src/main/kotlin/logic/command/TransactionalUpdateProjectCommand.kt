package org.example.logic.command

import org.example.logic.utils.ProjectNotChangedException

class TransactionalUpdateProjectCommand(private val commands: List<Command>) : Command {

    private val executedCommands = mutableListOf<Command>()

    override fun execute() {
        for (command in commands) {
            try {
                command.execute()
                executedCommands.add(command)
            } catch (e: Exception) {
                undo()
                throw ProjectNotChangedException("Project Not changed")

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