package com.stream.locomotion.data.repository

import com.stream.locomotion.data.remote.EpgParser
import com.stream.locomotion.data.remote.EpgService
import com.stream.locomotion.domain.model.Schedule
import com.stream.locomotion.domain.repository.ScheduleRepository
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val service: EpgService,
    private val parser: EpgParser
) : ScheduleRepository {

    override suspend fun fetchSchedule(): Schedule {
        var lastError: Throwable? = null
        repeat(2) {
            try {
                val xml = service.fetchSchedule()
                return parser.parse(xml, preferredChannelId = "Locomotion")
            } catch (t: Throwable) {
                lastError = t
            }
        }
        throw lastError ?: RuntimeException("Failed to fetch schedule")
    }
}
