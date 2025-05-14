package org.example.data.source.local.csv.utils.command


class TransactionalCommand(private val commands: List<Command>, private val exception:Exception=Exception()) : Command {


    private val executedCommands = mutableListOf<Command>()

    override suspend fun execute() {
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

    override suspend fun undo() {
        for (command in executedCommands.asReversed()) {
            command.undo()
        }
        executedCommands.clear()
    }
}