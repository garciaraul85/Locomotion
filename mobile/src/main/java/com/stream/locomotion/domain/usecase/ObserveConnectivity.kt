package com.stream.locomotion.domain.usecase

import com.stream.locomotion.domain.repository.ConnectivityRepository
import javax.inject.Inject

class ObserveConnectivity @Inject constructor(
    private val repository: ConnectivityRepository
) {
    operator fun invoke(): Boolean = repository.isOnline()
}
