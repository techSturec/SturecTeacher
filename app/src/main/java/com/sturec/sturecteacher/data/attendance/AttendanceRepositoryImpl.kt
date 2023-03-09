package com.sturec.sturecteacher.data.attendance

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.data.classroom_operations.ClassroomData
import com.sturec.sturecteacher.data.classroom_operations.ClassroomListStudentData
import com.sturec.sturecteacher.data.classroom_operations.ClassroomOperationsRepositoryImpl
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.data.user_data.UserDataDao
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AttendanceRepositoryImpl(
    val userDataDao: UserDataDao,
    private val classroomOperationsRepositoryImpl: ClassroomOperationsRepositoryImpl

) : AttendanceRepository {

    val reference = FirebaseDatabase.getInstance().reference

    override fun getAssignedClassroomList() = callbackFlow {
        classroomOperationsRepositoryImpl.getAssignedClassroomsList().collect {
            trySend(it)
        }
        awaitClose()
    }

    override fun getStudentsList(
        data: TeacherAssignedClassroomData) = callbackFlow {

        val schoolCode = userDataDao
            .getUserData(1)!!.schoolCode

        val sessionName = reference
            .child("sessionName")
            .get()
            .await()
            .value
            .toString()

        val listSnapshot = reference.child("schools")
            .child(schoolCode)
            .child("Classroom")
            .child(sessionName)
            .child(data.standard.toString())
            .child(data.section)
            .child("listOfStudents")
            .get()
            .await()

        val list = mutableListOf<ClassroomListStudentData>()

        for(i in listSnapshot.children)
        {
            list.add(i.getValue(ClassroomListStudentData::class.java)!!)
        }

        trySend(list)


        awaitClose()
    }

    override fun getSpecificDateAttendance(
        date:String, classData:TeacherAssignedClassroomData
    ) = callbackFlow{
        val schoolCode = userDataDao.getUserData(1)!!.schoolCode
        val sessionName = reference
            .child("sessionName")
            .get()
            .await()
            .value
            .toString()

        val data = AttendanceData()

        val dataSnapshot = reference.child("schools")
            .child(schoolCode)
            .child("Classroom")
            .child(sessionName)
            .child(classData.standard.toString())
            .child(classData.section)
            .child("attendance")
            .child(date)
            .get()
            .await()

        val absentListSnapshot = dataSnapshot.child("absentList")
        for(i in absentListSnapshot.children)
        {
            data.absentList.add(i.value.toString())
        }

        val leaveListSnapshot = dataSnapshot.child("leaveList")
        for(i in leaveListSnapshot.children)
        {
            data.leaveList.add(i.value.toString())
        }

        Log.e("checking data", data.toString())


        trySend(data)



        awaitClose()
    }

    override suspend fun uploadAttendanceOfSpecificDate(
        classData:TeacherAssignedClassroomData,
        attendanceData: AttendanceData,
        date:String
    ) {
        val schoolCode = userDataDao.getUserData(1)!!.schoolCode
        val sessionName = reference
            .child("sessionName")
            .get()
            .await()
            .value
            .toString()

        attendanceData.absentList

        val ref = reference.child("schools")
            .child(schoolCode)
            .child("Classroom")
            .child(sessionName)
            .child(classData.standard.toString())
            .child(classData.section)
            .child("attendance")
            .child(date)

        ref.child("absentList").setValue(attendanceData.absentList.toList())
        ref.child("leaveList").setValue(attendanceData.leaveList.toList())




    }

}