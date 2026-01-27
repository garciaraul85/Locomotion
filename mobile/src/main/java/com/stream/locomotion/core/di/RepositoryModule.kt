package com.stream.locomotion.core.di

import com.stream.locomotion.data.repository.ConnectivityRepositoryImpl
import com.stream.locomotion.data.repository.ScheduleRepositoryImpl
import com.stream.locomotion.data.repository.StreamRepositoryImpl
import com.stream.locomotion.domain.repository.ConnectivityRepository
import com.stream.locomotion.domain.repository.ScheduleRepository
import com.stream.locomotion.domain.repository.StreamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindStreamRepository(impl: StreamRepositoryImpl): StreamRepository

    @Binds
    @Singleton
    abstract fun bindConnectivityRepository(impl: ConnectivityRepositoryImpl): ConnectivityRepository
}
