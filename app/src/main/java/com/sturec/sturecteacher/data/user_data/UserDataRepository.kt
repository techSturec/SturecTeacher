package com.sturec.sturecteacher.data.user_data


interface UserDataRepository {

    suspend fun addUserData(userData: UserDataEntity)

    suspend fun getUserData(id: Int): UserDataEntity?
}