package com.stream.locomotion.data.local

import com.stream.locomotion.domain.model.Program
import com.stream.locomotion.domain.model.Schedule

fun ScheduleWithPrograms.toDomain(): Schedule {
    return Schedule(
        channelId = schedule.channelId,
        date = schedule.date,
        programs = programs.map { it.toDomain() }
    )
}

fun ProgramEntity.toDomain(): Program {
    return Program(
        id = id,
        channelId = channelId,
        title = title,
        description = description,
        startTime = startTime,
        endTime = endTime,
        iconUrl = iconUrl,
        year = year
    )
}

fun Schedule.toEntity(fetchedAt: Long): ScheduleEntity {
    return ScheduleEntity(
        channelId = channelId,
        date = date,
        fetchedAt = fetchedAt
    )
}

fun Program.toEntity(): ProgramEntity {
    return ProgramEntity(
        id = id,
        channelId = channelId,
        title = title,
        description = description,
        startTime = startTime,
        endTime = endTime,
        iconUrl = iconUrl,
        year = year
    )
}
