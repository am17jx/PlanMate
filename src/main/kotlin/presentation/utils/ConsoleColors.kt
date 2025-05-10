package presentation.utils

object ConsoleColors {
    const val RESET = "\u001B[0m"
    const val RED = "\u001B[91m"
    const val GREEN = "\u001B[92m"
    const val BLUE = "\u001B[32m"
    const val CYAN = "\u001B[96m"
}


fun String.red() = "${ConsoleColors.RED}$this${ConsoleColors.RESET}"
fun String.green() = "${ConsoleColors.GREEN}$this${ConsoleColors.RESET}"
fun String.blue() = "${ConsoleColors.BLUE}$this${ConsoleColors.RESET}"
fun String.cyan() = "${ConsoleColors.CYAN}$this${ConsoleColors.RESET}"