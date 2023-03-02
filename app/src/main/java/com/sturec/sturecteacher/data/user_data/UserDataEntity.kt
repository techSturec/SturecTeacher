package com.sturec.sturecteacher.data.user_data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserDataEntity(
    val schoolCode:String = "",
    @PrimaryKey val id:Int? = null
)