package com.stream.locomotion.data.repository

import android.content.Context
import com.stream.locomotion.R
import com.stream.locomotion.domain.repository.StreamRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StreamRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StreamRepository {
    override suspend fun streamUrls(): List<String> {
        return context.resources.getStringArray(R.array.stream_urls).toList()
    }
}
