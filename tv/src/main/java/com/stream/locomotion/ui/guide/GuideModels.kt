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
    val timeLabel: String
)

data class GuideRowUi(
    val channel: ChannelUi,
    val programs: List<ProgramUi>
)
