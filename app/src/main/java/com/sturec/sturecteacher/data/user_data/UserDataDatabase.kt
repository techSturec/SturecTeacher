package com.sturec.sturecteacher.data.user_data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserDataEntity::class],
    version = 1
)
abstract class UserDataDatabase:RoomDatabase() {
    abstract val dao:UserDataDao
}