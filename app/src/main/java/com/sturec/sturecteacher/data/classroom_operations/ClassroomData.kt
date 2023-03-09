package com.sturec.sturecteacher.data.classroom_operations

import com.sturec.sturecteacher.data.subject_operations.SubjectData

data class ClassroomData(
    val standard:Int = 0,
    val section: String = "",
    val inChargeMail: String = "",
    val className:String = "",
    val listOfStudents: MutableList<ClassroomListStudentData> = mutableListOf(),
    val subjects: List<SubjectData> = listOf()
)