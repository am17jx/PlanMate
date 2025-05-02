package presentation.utils.io

interface Reader {
    fun readString(): String

    fun readInt(): Int?

    fun readDouble(): Double?
}