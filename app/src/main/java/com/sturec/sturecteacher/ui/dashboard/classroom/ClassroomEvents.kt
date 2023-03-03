package com.sturec.sturecteacher.ui.dashboard.classroom

sealed class ClassroomEvents {
    object GetAssignedClassrooms : ClassroomEvents()
}