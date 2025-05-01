package presentation.utils.io

class ConsoleViewer : Viewer {
    override fun display(message: String?) {
        println(message)
    }
}