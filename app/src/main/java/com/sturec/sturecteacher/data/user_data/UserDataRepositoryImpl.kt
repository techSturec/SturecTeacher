package com.sturec.sturecteacher.data.user_data


class UserDataRepositoryImpl(
    private val dao:UserDataDao
):UserDataRepository {
    override suspend fun addUserData(userData: UserDataEntity) {
        dao.addUserData(userData)
    }

    override suspend fun getUserData(id: Int): UserDataEntity? {
        return dao.getUserData(id)
    }

}