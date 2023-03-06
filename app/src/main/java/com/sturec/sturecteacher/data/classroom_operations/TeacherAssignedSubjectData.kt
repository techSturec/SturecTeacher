package com.sturec.sturecteacher.data.classroom_operations

data class TeacherAssignedSubjectData(
    val subjectName: String = "",
    val teacherName: String = "",
    val teacherMail: String = "",
    val standard:Int = 0,
    val section: String = "",
    val className:String = "",
)