package com.stream.locomotion.ui.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stream.locomotion.domain.model.Schedule
import com.stream.locomotion.domain.usecase.FetchSchedule
import com.stream.locomotion.domain.usecase.ObserveConnectivity
import com.stream.locomotion.domain.usecase.ObserveSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GuideViewModel @Inject constructor(
    observeConnectivity: ObserveConnectivity,
    private val fetchSchedule: FetchSchedule,
    private val observeSchedule: ObserveSchedule
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = observeConnectivity()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    private val _uiState = MutableStateFlow(GuideUiState())
    val uiState: StateFlow<GuideUiState> = _uiState.asStateFlow()

    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    init {
        _uiState.value = GuideUiState(currentTimeLabel = formatTime(System.currentTimeMillis()))
        viewModelScope.launch {
            runCatching { fetchSchedule() }
        }
        viewModelScope.launch {
            observeSchedule().collect { schedule ->
                _uiState.value = buildUiState(schedule)
            }
        }
    }

    private fun buildUiState(schedule: Schedule): GuideUiState {
        val programs = schedule.programs.sortedBy { it.startTime }
        val shiftedPrograms = shiftProgramsToToday(programs)
        val now = System.currentTimeMillis()
        val windowStart = floorToHalfHour(now)
        val visiblePrograms = shiftedPrograms.filter { it.endTime > windowStart }
        val slotCount = visiblePrograms.size
        val timeSlots = buildHalfHourSlots(windowStart, slotCount)
        val rows = listOf(
            GuideRowUi(
                channel = ChannelUi(
                    id = schedule.channelId,
                    number = "1",
                    name = channelName(schedule.channelId),
                    subtitle = "Network"
                ),
                programs = visiblePrograms.map { program ->
                    val isCurrent = now in program.startTime until program.endTime
                    ProgramUi(
                        id = program.id,
                        title = program.title,
                        timeLabel = formatTime(program.startTime),
                        isCurrent = isCurrent,
                        startTime = program.startTime,
                        endTime = program.endTime
                    )
                }
            )
        )
        val currentIndex = visiblePrograms.indexOfFirst { now in it.startTime until it.endTime }
            .let { if (it >= 0) it else 0 }
        return GuideUiState(
            rows = rows,
            timeSlots = timeSlots,
            currentIndex = currentIndex,
            currentTimeLabel = formatTime(now)
        )
    }

    private fun channelName(channelId: String): String {
        return if (channelId.equals("Locomotion", ignoreCase = true)) "Locomotion" else channelId
    }

    private fun formatTime(timestamp: Long): String {
        return timeFormatter.format(Date(timestamp))
    }

    private fun shiftProgramsToToday(programs: List<com.stream.locomotion.domain.model.Program>): List<com.stream.locomotion.domain.model.Program> {
        if (programs.isEmpty()) return programs
        val firstStart = programs.first().startTime
        if (firstStart <= 0L) return programs
        val startDay = startOfDay(firstStart)
        val todayDay = startOfDay(System.currentTimeMillis())
        val shiftMs = todayDay - startDay
        if (shiftMs == 0L) return programs
        return programs.map { program ->
            program.copy(
                startTime = program.startTime + shiftMs,
                endTime = program.endTime + shiftMs
            )
        }
    }

    private fun startOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun floorToHalfHour(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        val minute = cal.get(Calendar.MINUTE)
        cal.set(Calendar.MINUTE, if (minute < 30) 0 else 30)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun buildHalfHourSlots(startMs: Long, count: Int): List<String> {
        if (count <= 0) return emptyList()
        return (0 until count).map { index ->
            formatTime(startMs + index * 30L * 60L * 1000L)
        }
    }
}
