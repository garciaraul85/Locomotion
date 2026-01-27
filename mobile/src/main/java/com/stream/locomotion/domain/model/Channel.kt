package com.stream.locomotion.domain.model

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val streamUrls: List<String>
)
