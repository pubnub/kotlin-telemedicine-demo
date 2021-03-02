package com.pubnub.demo.telemedicine.di.module

import android.content.res.Resources
import com.pubnub.demo.telemedicine.mapper.UserAvatarMapperImpl
import com.pubnub.demo.telemedicine.mapper.UserIdMapperImpl
import com.pubnub.demo.telemedicine.network.mapper.MetadataUserMapperImpl
import com.pubnub.demo.telemedicine.network.mapper.OccupancyMapperImpl
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.framework.mapper.OccupancyMapper
import com.pubnub.framework.mapper.UserAvatarMapper
import com.pubnub.framework.mapper.UserIdMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Singleton
    @Provides
    fun provideUserIdMapper(resources: Resources, userRepository: UserRepository): UserIdMapper =
        UserIdMapperImpl(resources, userRepository)

    @Singleton
    @Provides
    fun provideUserAvatarMapper(userRepository: UserRepository): UserAvatarMapper =
        UserAvatarMapperImpl(userRepository)

    @Singleton
    @Provides
    fun provideOccupancyMapper(): OccupancyMapper =
        OccupancyMapperImpl()

    @Singleton
    @Provides
    fun provideMetadataUserMapper(): MetadataUserMapperImpl =
        MetadataUserMapperImpl()
}
