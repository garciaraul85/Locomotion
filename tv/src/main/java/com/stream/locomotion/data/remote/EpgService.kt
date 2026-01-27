package com.stream.locomotion.data.remote

import retrofit2.http.GET

interface EpgService {
    @GET("guia.xml")
    suspend fun fetchSchedule(): String
}
