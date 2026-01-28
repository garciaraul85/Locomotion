package com.stream.locomotion.data.repository

import com.stream.locomotion.data.remote.EpgParser
import com.stream.locomotion.data.remote.EpgService
import com.stream.locomotion.data.local.ScheduleDao
import com.stream.locomotion.data.local.toDomain
import com.stream.locomotion.data.local.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.stream.locomotion.domain.model.Schedule
import com.stream.locomotion.domain.repository.ScheduleRepository
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val service: EpgService,
    private val parser: EpgParser,
    private val dao: ScheduleDao
) : ScheduleRepository {

    private val cacheTtlMs = 10 * 60 * 1000L

    override suspend fun fetchSchedule(): Schedule = withContext(Dispatchers.IO) {
        val cached = dao.getSchedule("Locomotion")
        val now = System.currentTimeMillis()
        if (cached != null && now - cached.schedule.fetchedAt <= cacheTtlMs) {
            return@withContext cached.toDomain()
        }

        var lastError: Throwable? = null
        repeat(2) {
            try {
                val xml = service.fetchSchedule()
                val schedule = parser.parse(xml, preferredChannelId = "Locomotion")
                dao.insertSchedule(schedule.toEntity(now))
                dao.deleteProgramsForChannel(schedule.channelId)
                dao.insertPrograms(schedule.programs.map { it.toEntity() })
                return@withContext schedule
            } catch (t: Throwable) {
                lastError = t
            }
        }

        if (cached != null) {
            return@withContext cached.toDomain()
        }

        throw lastError ?: RuntimeException("Failed to fetch schedule")
    }

    override fun observeSchedule(): Flow<Schedule> {
        return dao.observeSchedule("Locomotion")
            .filterNotNull()
            .map { it.toDomain() }
    }
}
