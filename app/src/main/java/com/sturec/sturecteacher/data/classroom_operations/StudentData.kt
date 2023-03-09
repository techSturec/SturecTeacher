package com.sturec.sturecteacher.data.classroom_operations

data class StudentData(
    var rollNo:String = "",
    val admissionNo:String = "",
    var studentName:String = "",
    var standard:Int = 0,
    var section: String = "",
    var className:String = "",
    val mail:String = ""
)