package org.example.logic.command

class TransactionCommands(private val commands: List<Command>) : Command {

    private val executedCommands = mutableListOf<Command>()

    override fun execute() {
        for (command in commands) {
            try {
                command.execute()
                executedCommands.add(command)
            } catch (e: Exception) {
                undo()
                throw e

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