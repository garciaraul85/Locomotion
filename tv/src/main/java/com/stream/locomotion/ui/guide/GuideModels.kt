package com.stream.locomotion.ui.guide

data class ChannelUi(
    val id: String,
    val number: String,
    val name: String,
    val subtitle: String
)

data class ProgramUi(
    val id: String,
    val title: String,
    val timeLabel: String,
    val isCurrent: Boolean = false,
    val startTime: Long = 0L,
    val endTime: Long = 0L
)

data class GuideRowUi(
    val channel: ChannelUi,
    val programs: List<ProgramUi>
)

data class GuideUiState(
    val rows: List<GuideRowUi> = emptyList(),
    val timeSlots: List<String> = emptyList(),
    val currentIndex: Int = 0,
    val currentTimeLabel: String = ""
)
