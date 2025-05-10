package org.example.presentation.role

abstract class SharedOptions : ProjectScreensOptions {
    override fun showAllProjectsOptions(): Map<String, String> {
        return mapOf(
                "1" to "1- choose project",
                "5" to "5- Back"
            )
    }
}