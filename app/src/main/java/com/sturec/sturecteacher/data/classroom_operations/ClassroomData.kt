package com.sturec.sturecteacher.data.classroom_operations

data class ClassroomData(
    val standard:Int = 0,
    val section: String = "",
    val inChargeMail: String = "",
    val className:String = "",
    val listOfStudents: MutableMap<String, StudentData> = mutableMapOf()
)