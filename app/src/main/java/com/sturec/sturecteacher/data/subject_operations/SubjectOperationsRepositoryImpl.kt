package com.sturec.sturecteacher.data.subject_operations

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.data.user_data.UserDataDao
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SubjectOperationsRepositoryImpl(
    private val userDataDao: UserDataDao
):SubjectOperationRepository {

    private val reference = FirebaseDatabase.getInstance().reference

    override fun addSubjectsForClassroom(
        classroomData: TeacherAssignedClassroomData,
        subjectData: SubjectData
    ) = callbackFlow {
        val schoolCode = userDataDao.getUserData(1)!!.schoolCode
        val sessionName = reference.child("sessionName").get().await().value.toString()
        val ref = reference.child("schools").child(schoolCode).child("Classroom")
            .child(sessionName).child(classroomData.standard.toString())
            .child(classroomData.section).child("subjects")
        val list = mutableListOf<SubjectData>()

        val dataReceived = ref.get().addOnFailureListener{
            trySend(false)
        }.await()

        for(i in dataReceived.children)
        {
            list.add(i.getValue(SubjectData::class.java)!!)
        }

        list.add(subjectData)


        ref.setValue(list).addOnFailureListener{
            trySend(false)
        }.addOnSuccessListener {
            trySend(true)
        }

        awaitClose()
    }

    override fun getAllSubjectsData(classroomData: TeacherAssignedClassroomData) = callbackFlow{
        val schoolCode = userDataDao.getUserData(1)!!.schoolCode
        val sessionName = reference.child("sessionName").get().await().value.toString()
        val ref = reference.child("schools").child(schoolCode).child("Classroom")
            .child(sessionName).child(classroomData.standard.toString()).child(classroomData.section)
            .child("subjects")

        val data = ref.get().await()

        val list: MutableList<SubjectData> = mutableListOf()
        for(i in data.children)
        {
            list.add(i.getValue(SubjectData::class.java)!!)
        }

        trySend(list)

        awaitClose()
    }


}