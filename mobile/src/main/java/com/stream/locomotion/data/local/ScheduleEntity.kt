package com.stream.locomotion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class ScheduleEntity(
    @PrimaryKey val channelId: String,
    val date: String,
    val fetchedAt: Long
)
