package com.stream.locomotion.data.repository

import com.stream.locomotion.domain.repository.ConnectivityRepository
import javax.inject.Inject

class ConnectivityRepositoryImpl @Inject constructor() : ConnectivityRepository {
    override fun isOnline(): Boolean = true
}
