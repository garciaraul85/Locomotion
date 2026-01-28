package com.stream.locomotion.core.di

import android.content.Context
import androidx.room.Room
import com.stream.locomotion.data.local.LocomotionDatabase
import com.stream.locomotion.data.local.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocomotionDatabase {
        return Room.databaseBuilder(context, LocomotionDatabase::class.java, "locomotion.db")
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideScheduleDao(db: LocomotionDatabase): ScheduleDao = db.scheduleDao()
}
