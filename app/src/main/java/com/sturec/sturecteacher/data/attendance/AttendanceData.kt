package com.sturec.sturecteacher.data.attendance

data class AttendanceData(
    val absentList:MutableSet<String> = mutableSetOf(),
    val leaveList:MutableSet<String> = mutableSetOf()
)
