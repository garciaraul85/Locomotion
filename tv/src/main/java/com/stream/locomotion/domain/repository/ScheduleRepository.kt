package com.stream.locomotion.domain.repository

import com.stream.locomotion.domain.model.Schedule

interface ScheduleRepository {
    suspend fun fetchSchedule(): Schedule
}
