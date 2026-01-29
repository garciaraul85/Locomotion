package com.stream.locomotion.domain.usecase

import com.stream.locomotion.domain.model.Schedule
import com.stream.locomotion.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSchedule @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<Schedule> = repository.observeSchedule()
}
