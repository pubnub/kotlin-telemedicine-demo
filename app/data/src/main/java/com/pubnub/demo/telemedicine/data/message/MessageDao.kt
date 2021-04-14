package com.pubnub.demo.telemedicine.data.message

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Transaction
    @Query("SELECT * FROM message WHERE id LIKE :messageId LIMIT 1")
    suspend fun get(messageId: String): MessageWithActions?

    @Transaction
    @Query("SELECT * FROM message WHERE channelId LIKE :channelId AND timestamp < :timestamp ORDER BY timestamp DESC LIMIT :count")
    suspend fun getBefore(channelId: String, count: Int, timestamp: Long): List<MessageWithActions>

    @Transaction
    @Query("SELECT * FROM message WHERE channelId LIKE :channelId AND timestamp > :timestamp ORDER BY timestamp DESC LIMIT :count")
    suspend fun getAfter(channelId: String, count: Int, timestamp: Long): List<MessageWithActions>

    @Transaction
    @Query("SELECT * FROM message WHERE channelId LIKE :channelId ORDER BY timestamp ASC LIMIT 1")
    fun getFirst(channelId: String): Flow<MessageWithActions?>

    @Transaction
    @Query("SELECT * FROM message WHERE channelId LIKE :channelId ORDER BY timestamp DESC LIMIT 1")
    fun getLast(channelId: String): Flow<MessageWithActions?>

    @Transaction
    @Query("SELECT * FROM message")
    fun getAll(): Flow<List<MessageWithActions>>

    @Transaction
    @Query("SELECT * FROM message WHERE userId LIKE :userId")
    fun getAll(userId: String): Flow<List<MessageWithActions>>

    @Query("SELECT * FROM message WHERE isSent LIKE 0")
    fun getUnsent(): Flow<List<MessageData>>

    @Transaction
    @Query("SELECT * FROM message WHERE channelId LIKE :channelId")
    fun getByChannel(channelId: String): Flow<List<MessageWithActions>>

    @Transaction
    @Query("SELECT * FROM message WHERE channelId LIKE :channelId ORDER BY timestamp DESC")
    fun getPageByChannel(channelId: String): PagingSource<Int, MessageWithActions>

    @Transaction
    @Query("SELECT * FROM message WHERE channelId LIKE :channelId ORDER BY timestamp DESC LIMIT :count")
    fun getLastByChannel(channelId: String, count: Long): Flow<List<MessageWithActions>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg message: MessageData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg message: MessageData)

    @Delete
    suspend fun delete(messageData: MessageData)
}
