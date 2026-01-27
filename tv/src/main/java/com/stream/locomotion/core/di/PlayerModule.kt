package com.stream.locomotion.core.di

import com.stream.locomotion.player.DefaultPlayerController
import com.stream.locomotion.player.PlayerController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {
    @Binds
    @Singleton
    abstract fun bindPlayerController(impl: DefaultPlayerController): PlayerController
}
