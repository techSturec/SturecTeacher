package com.sturec.sturecteacher.ui.dashboard.attendance

sealed class AttendanceEvents {
    object Navigate:AttendanceEvents()
    data class Snackbar(
        val message:String
    ):AttendanceEvents()
}