package com.sturec.sturecteacher.data.classroom_operations

import kotlinx.coroutines.flow.Flow

interface ClassroomOperationsRepository {
    fun getAssignedClassroomsList(): Flow<MutableList<TeacherAssignedClassroomData>>
    fun getAllClassroomsData(data: TeacherAssignedClassroomData):Flow<MutableList<ClassroomListStudentData>>

    fun addStudent(data:StudentData): Flow<Boolean>
}