package com.stream.locomotion.domain.usecase

import com.stream.locomotion.domain.model.Schedule
import com.stream.locomotion.domain.repository.ScheduleRepository
import javax.inject.Inject

class FetchSchedule @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(): Schedule = repository.fetchSchedule()
}
