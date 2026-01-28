package com.stream.locomotion.domain.repository

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    fun observeOnline(): Flow<Boolean>
    fun isOnline(): Boolean
}
