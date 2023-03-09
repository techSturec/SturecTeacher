package com.sturec.sturecteacher.data.attendance

import com.sturec.sturecteacher.data.classroom_operations.ClassroomListStudentData
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {

    fun getAssignedClassroomList(): Flow<MutableList<TeacherAssignedClassroomData>>

    fun getStudentsList(
        data:TeacherAssignedClassroomData
    ):Flow<MutableList<ClassroomListStudentData>>

    fun getSpecificDateAttendance(
        date:String,
        classData:TeacherAssignedClassroomData
    ):Flow<AttendanceData>

    suspend fun uploadAttendanceOfSpecificDate(
        classData:TeacherAssignedClassroomData,
        attendanceData: AttendanceData,
        date:String
    )
}