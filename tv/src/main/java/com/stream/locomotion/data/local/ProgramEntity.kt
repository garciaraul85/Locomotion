package com.stream.locomotion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "program")
data class ProgramEntity(
    @PrimaryKey val id: String,
    val channelId: String,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val iconUrl: String?,
    val year: Int?
)
