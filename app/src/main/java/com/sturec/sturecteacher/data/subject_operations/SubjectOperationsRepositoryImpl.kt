package com.sturec.sturecteacher.data.subject_operations

import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.data.classroom_operations.StudentData
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedSubjectData
import com.sturec.sturecteacher.data.user_data.UserDataDao
import com.sturec.sturecteacher.util.StringHashing
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SubjectOperationsRepositoryImpl(
    private val userDataDao: UserDataDao
):SubjectOperationRepository {

    private val reference = FirebaseDatabase.getInstance().reference
    private val hash = StringHashing()

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
        }

        val hashedMail = hash.createHash(subjectData.teacherMail)

        val listAssignedSubjects = mutableListOf<TeacherAssignedSubjectData>()
        val assignedSubjects = reference.child("schools").child(schoolCode)
            .child("Teacher").child("assignedSubjects")
            .child(hashedMail).get().await()

        for(i in assignedSubjects.children)
        {
            listAssignedSubjects.add(i.getValue(TeacherAssignedSubjectData::class.java)!!)
        }
        listAssignedSubjects.add(
            TeacherAssignedSubjectData(
                subjectName = subjectData.subjectName,
                teacherName = subjectData.teacherName,
                teacherMail = subjectData.teacherMail,
                standard = classroomData.standard,
                section = classroomData.section,
                className = classroomData.className
            )
        )

        reference.child("schools").child(schoolCode)
            .child("Teacher").child("assignedSubjects")
            .child(hashedMail).setValue(listAssignedSubjects).addOnSuccessListener {
                trySend(true)
            }.addOnFailureListener{
                trySend(false)
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

        if(list.isEmpty())
        {
            list.add(SubjectData())
        }

        trySend(list)

        awaitClose()
    }


}