package com.sturec.sturecteacher.data.bulletin

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.sturec.sturecteacher.data.user_data.UserDataDao
import com.sturec.sturecteacher.data.user_data.UserDataRepositoryImpl
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BulletinRepositoryImpl(
    private val userDataDao: UserDataDao
) : BulletinRepository {

    val reference = FirebaseDatabase.getInstance().reference

    override fun getAllAnnouncements() = callbackFlow {
        val schoolCode = userDataDao.getUserData(1)!!.schoolCode
//        Log.e("index", "found")

        val data = reference.child("schools").child(schoolCode).child("Teacher")
            .child("announcements").get().await()

        val list:MutableList<Pair<String, MutableList<AnnouncementData>>> = mutableListOf()

        for (i in data.children) {
            //dates
            val dateList:MutableList<AnnouncementData> = mutableListOf()
            for (j in i.children) {
                if (j.key != "index") {
                    dateList.add(j.getValue(AnnouncementData::class.java)!!)
                }
            }
            list.add(
                Pair(
                    i.key.toString(),
                    dateList
                )
            )
        }

        trySend(list)




        awaitClose()
    }
}