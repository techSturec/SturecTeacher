package com.sturec.sturecteacher.di

import android.app.Application
import androidx.room.Room
import com.sturec.sturecteacher.data.user_data.UserDataDatabase
import com.sturec.sturecteacher.data.user_data.UserDataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserDataDatabase(app:Application):UserDataDatabase {
        return Room.databaseBuilder(
            app,
            UserDataDatabase::class.java,
            "user_data_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDataRepository(userDataDatabase: UserDataDatabase):UserDataRepositoryImpl{
        return UserDataRepositoryImpl(userDataDatabase.dao)
    }
}