package com.pubnub.demo.telemedicine.di.module

import android.content.res.Resources
import com.pubnub.demo.telemedicine.data.chat.ChatDao
import com.pubnub.demo.telemedicine.mapper.ChatMapperImpl
import com.pubnub.demo.telemedicine.mapper.DoctorInfoMapperImpl
import com.pubnub.demo.telemedicine.network.service.MessageService
import com.pubnub.demo.telemedicine.repository.ChatRepository
import com.pubnub.demo.telemedicine.repository.ChatRepositoryImpl
import com.pubnub.demo.telemedicine.repository.MessageRepository
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.ui.conversation.Message
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Named
import javax.inject.Singleton

@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
)
@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    @Provides
    @Singleton
    fun provideChatRepository(
        dao: ChatDao,
        messageRepository: MessageRepository,
    ): ChatRepository =
        ChatRepositoryImpl(dao, messageRepository)


    @Named(ChatMapperImpl.GROUP_CHAT)
    @Provides
    @Singleton
    fun provideGroupChatMapper(
        resources: Resources,
        userRepository: UserRepository,
        messageService: MessageService<Message>,
    ): ChatMapperImpl = ChatMapperImpl(
        resources, userRepository, messageService, ChatMapperImpl.GROUP_CHAT
    )

    @Named(ChatMapperImpl.SINGLE_CHAT)
    @Provides
    @Singleton
    fun provideSingleChatMapper(
        resources: Resources,
        userRepository: UserRepository,
        messageService: MessageService<Message>,
    ): ChatMapperImpl = ChatMapperImpl(
        resources, userRepository, messageService, ChatMapperImpl.SINGLE_CHAT
    )

    @Provides
    @Singleton
    fun provideDoctorInfoMapper(
        resources: Resources,
    ): DoctorInfoMapperImpl = DoctorInfoMapperImpl(resources)
}
