package com.stream.locomotion.domain.repository

interface StreamRepository {
    suspend fun streamUrls(): List<String>
}
