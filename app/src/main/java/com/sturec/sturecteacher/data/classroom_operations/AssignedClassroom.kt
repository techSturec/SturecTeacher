package com.sturec.sturecteacher.data.classroom_operations

data class AssignedClassroom(
    val mail:String = "",
    val assignedClassrooms:MutableList<TeacherAssignedClassroomData> = mutableListOf()
)