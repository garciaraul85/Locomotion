package com.stream.locomotion.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class ScheduleWithPrograms(
    @Embedded val schedule: ScheduleEntity,
    @Relation(
        parentColumn = "channelId",
        entityColumn = "channelId"
    )
    val programs: List<ProgramEntity>
)
