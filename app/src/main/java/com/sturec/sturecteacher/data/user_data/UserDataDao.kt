package com.sturec.sturecteacher.data.user_data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserData(userData: UserDataEntity)

    @Query("SELECT * FROM userdataentity WHERE id = :id")
    suspend fun getUserData(id: Int): UserDataEntity?
}