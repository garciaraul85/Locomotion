package com.stream.locomotion.domain.model

data class Schedule(
    val channelId: String,
    val date: String,
    val programs: List<Program>
)
