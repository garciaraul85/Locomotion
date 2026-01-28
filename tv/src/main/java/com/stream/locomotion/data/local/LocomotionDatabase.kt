package com.stream.locomotion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ScheduleEntity::class, ProgramEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LocomotionDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}
