package com.sturec.sturecteacher.data.classroom_operations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.data.user_data.UserDataRepositoryImpl
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ClassroomOperationsRepositoryImpl(
    val userDataRepositoryImpl: UserDataRepositoryImpl
) : ClassroomOperationsRepository {

    private val reference = FirebaseDatabase.getInstance().reference
    private val authInstance = FirebaseAuth.getInstance()

    override fun getAssignedClassroomsList() = callbackFlow {
        val schoolCode = userDataRepositoryImpl.getUserData(1)!!.schoolCode
        val mail = authInstance.currentUser!!.email
        val data = reference.child("schools").child(schoolCode).child("Teacher")
            .child("assignedClassroomList").get().await()

        for (i in data.children) {
            val temp = i.getValue(AssignedClassroom::class.java)
            if (temp!!.mail == mail) {
                trySend(temp.assignedClassrooms)
            }
        }

        awaitClose()
    }

    override fun getAllClassroomsData(data: List<TeacherAssignedClassroomData>) = callbackFlow {
        //TODO("Not yet implemented")
        val schoolCode = userDataRepositoryImpl.getUserData(1)!!.schoolCode
        val dataMap: MutableMap<String, ClassroomData> = mutableMapOf()
        val sessionName = reference.child("sessionName").get().await().value.toString()

        for (i in data) {
            val result = reference.child("schools").child(schoolCode)
                .child("Classroom").child(sessionName).child(i.standard.toString())
                .child(i.section).get().await().getValue(ClassroomData::class.java)
            dataMap[i.className] = result!!
        }

        trySend(dataMap)

        awaitClose()
    }
}