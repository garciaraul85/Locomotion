package com.stream.locomotion.domain.model

data class Program(
    val id: String,
    val channelId: String,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val iconUrl: String?,
    val year: Int?
)
