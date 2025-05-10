package org.example.presentation.role

abstract class SharedOptions : ProjectScreensOptions {
    override fun showAllProjectsOptions(): Map<String, String> =
        mapOf(
            "1" to "1- choose project",
        )
}
