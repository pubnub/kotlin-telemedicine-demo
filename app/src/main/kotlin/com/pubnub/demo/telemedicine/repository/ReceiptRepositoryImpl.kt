package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.message.ReceiptDao
import com.pubnub.demo.telemedicine.data.message.ReceiptData
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class ReceiptRepositoryImpl(
    private val receiptDao: ReceiptDao,
) : ReceiptRepository {

    override fun get(id: String): ReceiptData? =
        runBlocking { receiptDao.get(id).first() }

    override fun getAll(): Flow<List<ReceiptData>> =
        receiptDao.getAll()
            .distinctUntilChanged()

    override fun getAll(channelId: String, messageTimestamp: Long): Flow<List<ReceiptData>> =
        receiptDao.getAll(channelId, messageTimestamp)
            .distinctUntilChanged()

    override fun getByChannel(channelId: String): Flow<List<ReceiptData>> =
        receiptDao.getByChannel(channelId)
            .distinctUntilChanged()

    override fun getByMessage(messageTimestamp: Long): Flow<List<ReceiptData>> =
        receiptDao.getByMessage(messageTimestamp)
            .distinctUntilChanged()

    override suspend fun isDelivered(
        channelId: String,
        messageTimestamp: Long,
        userId: UserId,
    ): Boolean =
        getAll(channelId, messageTimestamp)
            .firstOrNull()
            ?.any { it.action == ReceiptData.Type.DELIVERED && it.userId == userId } ?: false

    override suspend fun isRead(
        channelId: String,
        messageTimestamp: Long,
        userId: UserId,
    ): Boolean =
        getAll(channelId, messageTimestamp)
            .firstOrNull()
            ?.any { it.action == ReceiptData.Type.READ && it.userId == userId } ?: false

    override suspend fun getLastRead(
        channelId: String,
        userId: UserId,
    ): Flow<ReceiptData?> =
        receiptDao.getLastRead(userId, channelId, type = ReceiptData.Type.READ)

    override suspend fun getLastConfirmed(
        channelId: String,
        userId: UserId,
    ): Flow<ReceiptData?> =
        receiptDao.getLastConfirmed(userId, channelId, type = ReceiptData.Type.READ)

    @Synchronized
    override suspend fun add(vararg data: ReceiptData) {
        receiptDao.insert(*data)
    }

    @Synchronized
    override suspend fun update(vararg data: ReceiptData) {
        receiptDao.update(*data)
    }

    @Synchronized
    override suspend fun insertUpdate(vararg data: ReceiptData) {
        data.forEach { reaction ->
            if (has(reaction.id)) update(reaction)
            else add(reaction)
        }
    }

    override suspend fun delete(id: String) {
        get(id)?.let { receiptDao.delete(it) }
    }

    @Synchronized
    override fun has(receiptId: String): Boolean =
        get(receiptId) != null

    override suspend fun getLastTimestamp(): Long =
        receiptDao.getLast().first()?.actionTimestamp ?: 0L

    override suspend fun getLastTimestamp(channelId: String): Long =
        receiptDao.getLast(channelId).firstOrNull()?.actionTimestamp ?: 0L
}
