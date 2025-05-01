package org.example.presentation.role

abstract class SharedOptions : ProjectScreensOptions {
    override fun showAllProjectsOptions():List<Map<String,String>> {
        return listOf(mapOf("1" to "1- choose project id"))
    }

}