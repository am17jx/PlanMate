package presentation.utils.io

class ConsoleReader : Reader {
    override fun readString(): String = readln()

    override fun readInt(): Int? = readlnOrNull()?.toIntOrNull()

    override fun readDouble(): Double? = readlnOrNull()?.toDoubleOrNull()

}