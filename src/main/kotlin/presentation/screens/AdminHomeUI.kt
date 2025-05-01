package org.example.presentation.screens

class AdminHomeUI(
    private val onOptionSelected: (Int) -> Unit
) {
    init {
        showMenu()
    }

    private fun showMenu() {
        println("\n===== Admin Home =====")
        println("1. Show All Projects")
        println("2. Create New Project")
        println("3. Create User")
        println("4. Back")
        print("Enter your choice: ")

        val choice = readln().toIntOrNull()
        if (choice in 1..4) {
            onOptionSelected(choice!!)
        } else {
            println("Invalid input. Try again.")
            showMenu()
        }
    }
}
