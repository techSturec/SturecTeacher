package com.sturec.sturecteacher.data.classroom_operations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.data.user_data.UserDataRepositoryImpl
import com.sturec.sturecteacher.util.StringHashing
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ClassroomOperationsRepositoryImpl(
    val userDataRepositoryImpl: UserDataRepositoryImpl
) : ClassroomOperationsRepository {

    private val reference = FirebaseDatabase.getInstance().reference
    private val authInstance = FirebaseAuth.getInstance()
    private val hashing = StringHashing()

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
            .child(data.section).child("listOfStudents")
            .get()
            .await()

        val listOfStudents = mutableListOf<ClassroomListStudentData>()

        for(i in result.children)
        {
            listOfStudents.add(i.getValue(ClassroomListStudentData::class.java)!!)
        }
//        dataMap[data.className] = result!!

//        Log.e("result", result.toString())

        trySend(listOfStudents)

        awaitClose()
    }

    override fun addStudent(data: StudentData) = callbackFlow {
        // TODO: add students list to room database
        val schoolCode = userDataRepositoryImpl.getUserData(1)!!.schoolCode

        val sessionName = reference.child("sessionName").get().await().value.toString()

        var check = true

        val studentRef = reference.child("schools").child(schoolCode)
            .child("Student").child("profiles")
            .child(hashing.createHash(data.mail))

        val studentData = studentRef.get().await().getValue(StudentData::class.java)

        studentData?.let {
            studentData.rollNo = data.rollNo
            studentData.className = data.className
            studentData.section = data.section
            studentData.standard = data.standard
            studentData.studentName = data.studentName

            studentRef.setValue(studentData).addOnFailureListener {
                check = false
            }.await()
        }

//        Log.e("data", data.toString())
        val ref = reference.child("schools").child(schoolCode).child("Classroom")
            .child(sessionName).child(data.standard.toString()).child(data.section)
            .child("listOfStudents").child(hashing.createHash(data.mail))


        ref.setValue(
            ClassroomListStudentData(
                mail = data.mail,
                rollNo = data.rollNo,
                studentName = data.studentName
            )
        ).addOnFailureListener{
            check = false
        }.await()


        trySend(check)

//        trySend(false)
        awaitClose()
    }
}