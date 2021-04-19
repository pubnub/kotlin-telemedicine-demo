package com.pubnub.demo.telemedicine.data.message

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {

    @Query("SELECT * FROM message_receipt WHERE id LIKE :reactionId LIMIT 1")
    fun get(reactionId: String): Flow<ReceiptData?>

    @Query("SELECT * FROM message_receipt")
    fun getAll(): Flow<List<ReceiptData>>

    @Query("SELECT * FROM message_receipt WHERE channelId LIKE :channelId AND messageTimestamp LIKE :messageTimestamp")
    fun getAll(channelId: String, messageTimestamp: Long): Flow<List<ReceiptData>>

    @Query("SELECT * FROM message_receipt WHERE channelId LIKE :channelId")
    fun getByChannel(channelId: String): Flow<List<ReceiptData>>

    @Query("SELECT * FROM message_receipt WHERE messageTimestamp LIKE :messageTimestamp")
    fun getByMessage(messageTimestamp: Long): Flow<List<ReceiptData>>

    @Query("SELECT * FROM message_receipt ORDER BY messageTimestamp DESC LIMIT 1")
    fun getLast(): Flow<ReceiptData?>

    @Query("SELECT * FROM message_receipt WHERE channelId LIKE :channelId ORDER BY messageTimestamp DESC LIMIT 1")
    fun getLast(channelId: String): Flow<ReceiptData?>

    // own messages, other user receipts
    @Query("SELECT * FROM message_receipt WHERE channelId LIKE :channelId AND `action` LIKE :type AND userId NOT LIKE :userId ORDER BY messageTimestamp DESC LIMIT 1")
    fun getLastRead(userId: UserId, channelId: String, type: String): Flow<ReceiptData?>

    // last message read confirmation, other user message, own receipt
    @Query("SELECT * FROM message_receipt WHERE channelId LIKE :channelId AND `action` LIKE :type AND userId LIKE :userId ORDER BY messageTimestamp DESC LIMIT 1")
    fun getLastConfirmed(userId: UserId, channelId: String, type: String): Flow<ReceiptData?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg reaction: ReceiptData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg reaction: ReceiptData)

    @Delete
    suspend fun delete(reaction: ReceiptData)
}
