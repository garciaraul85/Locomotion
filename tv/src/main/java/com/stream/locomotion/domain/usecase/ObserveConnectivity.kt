package com.stream.locomotion.domain.usecase

import com.stream.locomotion.domain.repository.ConnectivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectivity @Inject constructor(
    private val repository: ConnectivityRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.observeOnline()
}
