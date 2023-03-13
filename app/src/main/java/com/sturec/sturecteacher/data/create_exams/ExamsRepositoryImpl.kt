package com.sturec.sturecteacher.data.create_exams

import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.data.classroom_operations.ClassroomOperationsRepositoryImpl
import com.sturec.sturecteacher.data.classroom_operations.StudentData
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.data.subject_operations.SubjectData
import com.sturec.sturecteacher.data.subject_operations.SubjectOperationsRepositoryImpl
import com.sturec.sturecteacher.data.user_data.UserDataDao
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.tasks.await

class ExamsRepositoryImpl(
    private val classroomOperationsRepositoryImpl: ClassroomOperationsRepositoryImpl,
    private val userDataDao: UserDataDao
):ExamsRepository {

    private val reference = FirebaseDatabase.getInstance().reference

    override fun getAssignedClassrooms() = callbackFlow{
        //TODO("Not yet implemented")
        classroomOperationsRepositoryImpl.getAssignedClassroomsList().collect{
            trySend(it)
        }

        awaitClose()
    }

    override fun getSubjectsList(classroom:TeacherAssignedClassroomData) = callbackFlow {
        //TODO("Not yet implemented")
        val sessionName = reference.child("sessionName").get().await().value.toString()
        val schoolCode = userDataDao.getUserData(1)!!.schoolCode
        val data = reference.child("schools/$schoolCode/Classroom/$sessionName/" +
                "${classroom.standard}/${classroom.section}/subjects")
            .get()
            .await()

        val list = mutableListOf<SubjectData>()

        for(i in data.children){
            list.add(i.getValue(SubjectData::class.java)!!)
        }

        trySend(list)
        awaitClose()
    }

    override fun getExamsList(
        classroom:TeacherAssignedClassroomData,
        subjectData: SubjectData
    ) = callbackFlow{
        //TODO("Not yet implemented")
        val sessionName = reference.child("sessionName").get().await().value.toString()
        val schoolCode = userDataDao.getUserData(1)!!.schoolCode
        val data = reference.child("schools/$schoolCode/Classroom/$sessionName/" +
                "${classroom.standard}/${classroom.section}/subjects/${subjectData.subjectName}" +
                "/examsList").get().await()

        val list = mutableListOf<ExamData>()

        for(i in data.children)
        {
            list.add(i.getValue(ExamData::class.java)!!)
        }

        trySend(list)

        awaitClose()
    }

    override suspend fun uploadNewExam(
        classroom:TeacherAssignedClassroomData,
        examData:ExamData
    ) {
        //TODO("Not yet implemented")
        val sessionName = reference.child("sessionName").get().await().value.toString()
        val schoolCode = userDataDao.getUserData(1)!!.schoolCode
        reference.child("schools/$schoolCode/Classroom/$sessionName/" +
                "${classroom.standard}/${classroom.section}/subjects/${examData.subjectName}" +
                "/examsList/${examData.title}").setValue(examData)
    }
}