package com.sturec.sturecteacher.data.subject_operations

import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import kotlinx.coroutines.flow.Flow

interface SubjectOperationRepository {

    fun addSubjectsForClassroom(classroomData:TeacherAssignedClassroomData, subjectData:SubjectData):Flow<Boolean>

    fun getAllSubjectsData(classroomData: TeacherAssignedClassroomData): Flow<MutableList<SubjectData>>
}