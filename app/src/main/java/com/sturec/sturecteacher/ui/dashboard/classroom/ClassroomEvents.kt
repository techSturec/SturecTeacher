package com.sturec.sturecteacher.ui.dashboard.classroom

import com.sturec.sturecteacher.data.classroom_operations.StudentData

sealed class ClassroomEvents {

    data class OnSaveStudentButtonClicked(
        val data:StudentData
    ):ClassroomEvents()
}