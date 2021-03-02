package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.message.ReceiptData
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

interface ReceiptRepository {
    fun has(receiptId: String): Boolean
    fun get(id: String): ReceiptData?
    fun getAll(): Flow<List<ReceiptData>>
    fun getAll(channelId: ChannelId, messageTimestamp: Long): Flow<List<ReceiptData>>
    fun getByChannel(ChannelId: String): Flow<List<ReceiptData>>
    fun getByMessage(messageTimestamp: Long): Flow<List<ReceiptData>>
    suspend fun isDelivered(channelId: String, messageTimestamp: Long, userId: UserId): Boolean
    suspend fun isRead(channelId: String, messageTimestamp: Long, userId: UserId): Boolean
    suspend fun getLastRead(channelId: String, userId: UserId): Flow<ReceiptData?>
    suspend fun add(vararg data: ReceiptData)
    suspend fun update(vararg data: ReceiptData)
    suspend fun insertUpdate(vararg data: ReceiptData)
    suspend fun delete(id: String)

    suspend fun getLastTimestamp(): Long
    suspend fun getLastTimestamp(channelId: ChannelId): Long
}
