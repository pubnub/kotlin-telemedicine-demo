package com.pubnub.demo.telemedicine.di.module

import com.pubnub.demo.telemedicine.data.channel.ChannelDao
import com.pubnub.demo.telemedicine.data.channel.ChannelGroupDao
import com.pubnub.demo.telemedicine.data.member.MemberDao
import com.pubnub.demo.telemedicine.network.service.ChannelGroupService
import com.pubnub.demo.telemedicine.network.service.UserService
import com.pubnub.demo.telemedicine.repository.ChannelGroupRepository
import com.pubnub.demo.telemedicine.repository.ChannelGroupRepositoryImpl
import com.pubnub.demo.telemedicine.repository.ChannelRepository
import com.pubnub.demo.telemedicine.repository.ChannelRepositoryImpl
import com.pubnub.demo.telemedicine.repository.MemberRepository
import com.pubnub.demo.telemedicine.repository.MemberRepositoryImpl
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.framework.mapper.OccupancyMapper
import com.pubnub.framework.mapper.UserIdMapper
import com.pubnub.framework.service.OccupancyService
import com.pubnub.framework.service.TypingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import javax.inject.Singleton
import com.pubnub.demo.telemedicine.network.service.ChannelService as ChannelNetworkService

@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
)
@Module
@InstallIn(SingletonComponent::class)
object ChannelModule {
    @Provides
    @Singleton
    fun provideChannelRepository(
        channelDao: ChannelDao,
        memberRepository: MemberRepository,
        channelGroupRepository: ChannelGroupRepository,
    ): ChannelRepository =
        ChannelRepositoryImpl(channelDao, memberRepository, channelGroupRepository)

    @Provides
    @Singleton
    fun provideMemberRepository(
        memberDao: MemberDao,
    ): MemberRepository =
        MemberRepositoryImpl(memberDao)

    @Provides
    @Singleton
    fun provideChannelGroupRepository(
        channelGroupDao: ChannelGroupDao,
    ): ChannelGroupRepository =
        ChannelGroupRepositoryImpl(channelGroupDao)

    @Provides
    @Singleton
    fun provideChannelNetworkService(
        channelRepository: ChannelRepository,
        channelGroupService: ChannelGroupService,
        userService: UserService,
        userRepository: UserRepository,
    ): ChannelNetworkService =
        ChannelNetworkService(
            channelRepository,
            channelGroupService,
            userService,
            userRepository,
        )

    @Provides
    @Singleton
    fun provideChannelGroupService(
        channelGroupRepository: ChannelGroupRepository,
        channelRepository: ChannelRepository,
    ): ChannelGroupService = ChannelGroupService(channelGroupRepository, channelRepository)

    @Provides
    @Singleton
    fun provideOccupancyService(occupancyMapper: OccupancyMapper): OccupancyService =
        OccupancyService(occupancyMapper)

    @ObsoleteCoroutinesApi
    @Provides
    @Singleton
    fun provideTypingService(
        occupancyService: OccupancyService,
        userIdMapper: UserIdMapper,
    ): TypingService = TypingService(
        occupancyService,
        userIdMapper
    )
}
