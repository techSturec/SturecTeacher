package com.sturec.sturecteacher.data.classroom_operations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.data.user_data.UserDataRepositoryImpl
import kotlinx.coroutines.channels.awaitClose
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

    override fun getAllClassroomsData(data: TeacherAssignedClassroomData) = callbackFlow {
        val schoolCode = userDataRepositoryImpl.getUserData(1)!!.schoolCode
//        val dataMap: MutableMap<String, ClassroomData> = mutableMapOf()
        val sessionName = reference.child("sessionName").get().await().value.toString()


        val result = reference.child("schools").child(schoolCode)
            .child("Classroom").child(sessionName).child(data.standard.toString())
            .child(data.section)
            .get()
            .await()
            .getValue(ClassroomData::class.java)
//        dataMap[data.className] = result!!

//        Log.e("result", result.toString())

        trySend(result!!)

        awaitClose()
    }

    override fun addStudent(data: StudentData) = callbackFlow {
        // TODO: add students list to room database
        val schoolCode = userDataRepositoryImpl.getUserData(1)!!.schoolCode

        val sessionName = reference.child("sessionName").get().await().value.toString()

//        Log.e("data", data.toString())
        val ref = reference.child("schools").child(schoolCode).child("Classroom")
            .child(sessionName).child(data.standard.toString()).child(data.section)
            .child("listOfStudents")


        val listOfStudent = mutableListOf<StudentData>()
        val receivedData = ref.get().await()
        for (i in receivedData.children) {
            i.getValue(StudentData::class.java)?.let {
                listOfStudent.add(it)
            }
        }
        listOfStudent.add(data)

        listOfStudent.sortBy {
            it.rollNo
        }


        ref.setValue(listOfStudent).addOnFailureListener {
            trySend(false)
        }.addOnSuccessListener {
            trySend(true)
        }

//        trySend(false)
        awaitClose()
    }
}