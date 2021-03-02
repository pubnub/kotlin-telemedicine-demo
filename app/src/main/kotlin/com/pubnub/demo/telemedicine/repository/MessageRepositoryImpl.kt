package com.pubnub.demo.telemedicine.repository

import androidx.paging.PagingSource
import com.pubnub.demo.telemedicine.data.message.MessageDao
import com.pubnub.demo.telemedicine.data.message.MessageData
import com.pubnub.demo.telemedicine.data.message.MessageWithActions
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MessageRepositoryImpl(
    private val messageDao: MessageDao,
) : MessageRepository {

    override suspend fun get(messageId: String): MessageWithActions? =
        messageDao.get(messageId)

    override suspend fun get(
        channelId: String,
        count: Int,
        before: Boolean,
        timestamp: Long,
    ): List<MessageWithActions> =
        if (before) messageDao.getBefore(channelId = channelId,
            count = count,
            timestamp = timestamp)
        else messageDao.getAfter(channelId = channelId, count = count, timestamp = timestamp)

    override suspend fun has(messageId: String): Boolean =
        get(messageId) != null

    override fun getAll(): Flow<List<MessageWithActions>> =
        messageDao.getAll()
    //.distinctUntilChanged()

    override fun getAll(channelId: String): Flow<List<MessageWithActions>> =
        messageDao.getByChannel(channelId)
    //.distinctUntilChanged()

    override fun getAllByUser(userId: String): Flow<List<MessageWithActions>> =
        messageDao.getAll(userId)
            .distinctUntilChanged()

    override suspend fun getLast(channelId: String): MessageWithActions? =
        messageDao.getLast(channelId).firstOrNull()

    override suspend fun hasMoreBefore(channelId: String, timestamp: Timetoken): Boolean =
        messageDao.getBefore(channelId, 1, timestamp).isNotEmpty()

    override suspend fun hasMoreAfter(channelId: String, timestamp: Timetoken): Boolean =
        messageDao.getAfter(channelId, 1, timestamp).isNotEmpty()


    override fun getPage(channelId: String): PagingSource<Int, MessageWithActions> =
        runBlocking {
            withContext(Dispatchers.IO) {
                messageDao.getPageByChannel(channelId)
            }
        }

    override fun getLastByChannel(
        channelId: String,
        count: Long,
    ): Flow<List<MessageWithActions>> =
        messageDao.getLastByChannel(channelId, count)


    override suspend fun add(channelId: String, message: MessageData) {
        messageDao.insert(message)
    }

    override suspend fun update(channelId: String, message: MessageData) {
        messageDao.update(message)
    }

    override suspend fun setStatus(
        messageId: String,
        isSent: Boolean,
        exception: String?,
        timestamp: Timetoken?,
    ) {
        val message = get(messageId)?.toMessageData()
            ?: throw Exception("Message not found exception")
        val updatedMessage = message.copy(
            isSent = isSent,
            exception = exception,
            timestamp = timestamp ?: message.timestamp,
        )
        messageDao.update(updatedMessage)
    }

    override suspend fun setSent(messageId: String, timestamp: Timetoken?) {
        setStatus(
            messageId = messageId,
            isSent = true,
            exception = null,
            timestamp = timestamp,
        )
    }

    override suspend fun setSendingError(
        messageId: String,
        exception: String?,
        timestamp: Timetoken?,
    ) {
        setStatus(
            messageId = messageId,
            isSent = false,
            exception = exception,
            timestamp = timestamp,
        )
    }

    override suspend fun getLastTimestamp(channelId: String): Long =
        messageDao.getLast(channelId).firstOrNull()?.timestamp ?: 1L

}
