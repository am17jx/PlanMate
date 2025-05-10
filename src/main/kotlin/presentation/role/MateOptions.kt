package org.example.presentation.role

class MateOptions : SharedOptions() {
    override fun showAllProjectsOptions(): Map<String, String> =
        super.showAllProjectsOptions() +
            mapOf(
                "5" to "5- Logout",
                "0" to "0- Exit",
            )
}
