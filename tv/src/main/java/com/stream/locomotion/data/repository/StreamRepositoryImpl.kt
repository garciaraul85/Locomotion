package com.stream.locomotion.data.repository

import com.stream.locomotion.domain.repository.StreamRepository
import javax.inject.Inject

class StreamRepositoryImpl @Inject constructor() : StreamRepository {
    override suspend fun streamUrls(): List<String> = emptyList()
}
