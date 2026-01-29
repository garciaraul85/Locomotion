package com.stream.locomotion.data.remote

import retrofit2.http.GET
import retrofit2.http.Url

interface EpgService {
    @GET
    suspend fun fetchSchedule(@Url url: String): String
}
