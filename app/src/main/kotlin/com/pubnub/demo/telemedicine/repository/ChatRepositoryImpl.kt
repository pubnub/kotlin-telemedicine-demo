package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.chat.ChatDao
import com.pubnub.demo.telemedicine.data.chat.ChatData
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Chat Repository Implementation
 *
 * Will manage chat in database
 */
@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
)
class ChatRepositoryImpl(
    private val dao: ChatDao,
    private val messageRepository: MessageRepository, // just for listening for new messages
) : ChatRepository {

    /**
     * Get chat by provided channel id
     * @param id of the chat / channel
     *
     * @return [ChatData]
     */
    override suspend fun get(id: ChannelId): ChatData? =
        dao.get(id).first()

    /**
     * Get list of chats for passed user id
     * @param userId to get chats
     *
     * Small workaround to get notified about chat changes added - @DatabaseView projection doesn't have listeners
     *
     * @return Flow with list of [ChatData] sorted descending by [ChatData.readAt]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAll(userId: UserId): Flow<List<ChatData>> =
        messageRepository.getAll() // just map for event
            .flatMapLatest { dao.getAll(userId) }
            .distinctUntilChanged()
            .map { list -> list.sortedByDescending { it.readAt } }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPatientChats(userId: UserId): Flow<List<ChatData>> =
        messageRepository.getAll() // just map for event
            .flatMapLatest { dao.getPatientChats(userId) }
            .distinctUntilChanged()
            .map { list -> list.sortedByDescending { it.readAt } }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDoctorChats(userId: UserId): Flow<List<ChatData>> =
        messageRepository.getAll() // just map for event
            .flatMapLatest { dao.getDoctorChats(userId) }
            .distinctUntilChanged()
            .map { list -> list.sortedByDescending { it.readAt } }

    /**
     * Set Chat readAt timestamp
     */
    override suspend fun setReadAt(
        chatId: String,
        timestamp: Timetoken,
    ) {
        Timber.e("Set read on '$chatId'")
        dao.setReadAt(timestamp, chatId)
    }

    override fun getUnreadCount(userId: String): Flow<Long> =
        dao.getUnreadCount(userId).map { it ?: 0 }
}
