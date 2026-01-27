package com.stream.locomotion.domain.repository

interface ConnectivityRepository {
    fun isOnline(): Boolean
}
