package com.sturec.sturecteacher.data.create_exams

import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.data.subject_operations.SubjectData
import kotlinx.coroutines.flow.Flow

interface ExamsRepository {

    fun getAssignedClassrooms(): Flow<MutableList<TeacherAssignedClassroomData>>

    fun getSubjectsList(classroom:TeacherAssignedClassroomData): Flow<MutableList<SubjectData>>

    fun getExamsList(
        classroom:TeacherAssignedClassroomData,
        subjectData: SubjectData
    ): Flow<MutableList<ExamData>>

    suspend fun uploadNewExam(
        classroom:TeacherAssignedClassroomData,
        examData:ExamData
    )

}