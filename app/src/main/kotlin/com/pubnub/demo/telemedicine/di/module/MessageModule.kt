package com.pubnub.demo.telemedicine.di.module

import android.content.res.Resources
import com.pubnub.demo.telemedicine.data.message.MessageDao
import com.pubnub.demo.telemedicine.data.message.ReceiptDao
import com.pubnub.demo.telemedicine.mapper.MessageMapperImpl
import com.pubnub.demo.telemedicine.mapper.MessageWithActionsMapperImpl
import com.pubnub.demo.telemedicine.network.mapper.HistoryMapperImpl
import com.pubnub.demo.telemedicine.network.service.ChannelService
import com.pubnub.demo.telemedicine.network.service.MessageService
import com.pubnub.demo.telemedicine.network.service.ReceiptService
import com.pubnub.demo.telemedicine.repository.MessageRepository
import com.pubnub.demo.telemedicine.repository.MessageRepositoryImpl
import com.pubnub.demo.telemedicine.repository.ReceiptRepository
import com.pubnub.demo.telemedicine.repository.ReceiptRepositoryImpl
import com.pubnub.demo.telemedicine.ui.conversation.Message
import com.pubnub.framework.mapper.UserAvatarMapper
import com.pubnub.framework.mapper.UserIdMapper
import com.pubnub.framework.service.ActionService
import com.pubnub.framework.service.FileService
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
object MessageModule {

    @Provides
    @Singleton
    fun provideReceiptRepository(receiptDao: ReceiptDao): ReceiptRepository =
        ReceiptRepositoryImpl(receiptDao)

    @Provides
    @Singleton
    fun provideMessageRepository(messageDao: MessageDao): MessageRepository =
        MessageRepositoryImpl(messageDao)

    @Provides
    @Singleton
    fun provideMessageMapper(
        resources: Resources,
        userIdMapper: UserIdMapper,
        userAvatarMapper: UserAvatarMapper,
    ): MessageMapperImpl =
        MessageMapperImpl(resources, userIdMapper, userAvatarMapper)

    @Provides
    @Singleton
    fun provideMessageWithReactionMapper(
        resources: Resources,
        userIdMapper: UserIdMapper,
        userAvatarMapper: UserAvatarMapper,
    ): MessageWithActionsMapperImpl =
        MessageWithActionsMapperImpl(resources, userIdMapper, userAvatarMapper)

    @Provides
    @Singleton
    fun provideHistoryMapper(): HistoryMapperImpl =
        HistoryMapperImpl()

    @Provides
    @Singleton
    fun provideFileService(): FileService =
        FileService()

    @Provides
    @Singleton
    fun provideActionService(): ActionService =
        ActionService()

    @Provides
    @Singleton
    fun provideReceiptService(
        actionService: ActionService,
        receiptRepository: ReceiptRepository,
    ): ReceiptService =
        ReceiptService(actionService, receiptRepository)

    @Provides
    @Singleton
    fun provideMessageService(
        messageRepository: MessageRepository,
        channelService: ChannelService,
        messageMapper: MessageWithActionsMapperImpl,
        historyMapper: HistoryMapperImpl,
    ): MessageService<Message> =
        MessageService(
            messageRepository,
            channelService,
            messageMapper,
            historyMapper,
        )
}
