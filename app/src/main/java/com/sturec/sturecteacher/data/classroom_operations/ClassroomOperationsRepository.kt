package com.sturec.sturecteacher.data.classroom_operations

import kotlinx.coroutines.flow.Flow

interface ClassroomOperationsRepository {
    fun getAssignedClassroomsList(): Flow<MutableList<TeacherAssignedClassroomData>>
    fun getAllClassroomsData(data: List<TeacherAssignedClassroomData>):Flow<MutableMap<String, ClassroomData>>
}