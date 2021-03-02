package com.pubnub.demo.telemedicine.di.module

import android.app.Application
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ContextModule {
    @Provides
    fun provideResources(app: Application): Resources = app.resources
}
