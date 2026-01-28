package com.stream.locomotion.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Transaction
    @Query("SELECT * FROM schedule WHERE channelId = :channelId LIMIT 1")
    fun observeSchedule(channelId: String): Flow<ScheduleWithPrograms?>

    @Transaction
    @Query("SELECT * FROM schedule WHERE channelId = :channelId LIMIT 1")
    suspend fun getSchedule(channelId: String): ScheduleWithPrograms?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(programs: List<ProgramEntity>)

    @Query("DELETE FROM program WHERE channelId = :channelId")
    suspend fun deleteProgramsForChannel(channelId: String)
}
