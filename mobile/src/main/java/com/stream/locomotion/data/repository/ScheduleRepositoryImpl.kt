package com.stream.locomotion.data.repository

import com.stream.locomotion.domain.model.Schedule
import com.stream.locomotion.domain.repository.ScheduleRepository
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor() : ScheduleRepository {
    override suspend fun fetchSchedule(): Schedule {
        return Schedule(channelId = "", date = "", programs = emptyList())
    }
}
