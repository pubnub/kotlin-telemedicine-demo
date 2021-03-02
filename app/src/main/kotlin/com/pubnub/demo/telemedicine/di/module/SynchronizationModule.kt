package com.pubnub.demo.telemedicine.di.module

import com.pubnub.demo.telemedicine.network.service.ChannelService
import com.pubnub.demo.telemedicine.network.service.SynchronizationService
import com.pubnub.demo.telemedicine.network.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
)
@Module
@InstallIn(SingletonComponent::class)
object SynchronizationModule {
    @Provides
    @Singleton
    fun provideSynchronizationService(
        channelService: ChannelService,
        userService: UserService,
    ): SynchronizationService = SynchronizationService(channelService, userService)
}
