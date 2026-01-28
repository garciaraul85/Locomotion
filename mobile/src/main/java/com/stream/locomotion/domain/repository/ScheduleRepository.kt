package com.stream.locomotion.domain.repository

import com.stream.locomotion.domain.model.Schedule

import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    suspend fun fetchSchedule(): Schedule
    fun observeSchedule(): Flow<Schedule>
}
