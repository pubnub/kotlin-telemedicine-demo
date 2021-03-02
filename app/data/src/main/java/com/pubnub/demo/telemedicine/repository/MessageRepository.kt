package com.pubnub.demo.telemedicine.repository

import androidx.paging.PagingSource
import com.pubnub.demo.telemedicine.data.message.MessageData
import com.pubnub.demo.telemedicine.data.message.MessageWithActions
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun get(messageId: String): MessageWithActions?
    fun getAll(): Flow<List<MessageWithActions>>
    fun getAll(channelId: String): Flow<List<MessageWithActions>>
    fun getAllByUser(userId: String): Flow<List<MessageWithActions>>
    suspend fun getLast(channelId: String): MessageWithActions?
    suspend fun hasMoreBefore(channelId: String, timestamp: Timetoken): Boolean
    suspend fun hasMoreAfter(channelId: String, timestamp: Timetoken): Boolean
    fun getLastByChannel(channelId: String, count: Long): Flow<List<MessageWithActions>>
    suspend fun get(
        channelId: String,
        count: Int,
        before: Boolean,
        timestamp: Long,
    ): List<MessageWithActions>

    fun getPage(channelId: String): PagingSource<Int, MessageWithActions>
    suspend fun add(channelId: String, message: MessageData)
    suspend fun update(channelId: String, message: MessageData)
    suspend fun has(messageId: String): Boolean
    suspend fun setStatus(
        messageId: String,
        isDelivered: Boolean = true,
        exception: String? = null,
        timestamp: Timetoken? = null,
    )

    suspend fun setSent(
        messageId: String,
        timestamp: Timetoken? = null,
    )

    suspend fun setSendingError(
        messageId: String,
        exception: String? = null,
        timestamp: Timetoken? = null,
    )

    suspend fun getLastTimestamp(channelId: String): Long
}
