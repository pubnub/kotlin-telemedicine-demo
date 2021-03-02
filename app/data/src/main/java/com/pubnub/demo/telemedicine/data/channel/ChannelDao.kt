package com.pubnub.demo.telemedicine.data.channel

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Query("SELECT * FROM `channel` WHERE id LIKE :channelId LIMIT 1")
    suspend fun get(channelId: String): ChannelData?

    @Query("SELECT * FROM `channel` WHERE id IN (:channelId)")
    fun getList(vararg channelId: String): Flow<List<ChannelData>>

    @Query("SELECT * FROM `channel` WHERE custom_patient LIKE :patientId AND custom_type IN (:type)")
    fun getAllByPatient(
        patientId: String,
        @ChannelTypeDef vararg type: String = ChannelType.ALL,
    ): Flow<List<ChannelData>>

    @Query("SELECT * FROM `channel` WHERE custom_doctors LIKE '%' || :doctorId || '%' AND custom_type IN (:type)")
    fun getAllByDoctor(
        doctorId: String,
        @ChannelTypeDef vararg type: String = ChannelType.ALL,
    ): Flow<List<ChannelData>>

    @Query("SELECT * FROM `channel` WHERE custom_type IN (:type) AND (custom_doctors LIKE '%' || :userId || '%' OR custom_patient LIKE :userId)")
    fun getAllByType(
        userId: UserId,
        @ChannelTypeDef vararg type: String = ChannelType.ALL,
    ): Flow<List<ChannelData>>

    @Query("SELECT * FROM `channel` WHERE custom_type LIKE :type AND custom_doctors LIKE '%' || :userId || '%' AND (custom_doctors LIKE '%' || :contactId || '%' OR custom_patient LIKE :contactId)")
    suspend fun getConversation(
        userId: UserId,
        contactId: UserId,
        @ChannelTypeDef type: String = ChannelType.CHAT,
    ): ChannelData?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg data: ChannelData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg data: ChannelData)

    @Delete
    suspend fun delete(data: ChannelData)

    @Query("DELETE FROM `channel` WHERE id LIKE :channelId")
    suspend fun delete(channelId: String)

    @Query("UPDATE `channel` SET custom_readAt = :timestamp WHERE id IN (:channelId)")
    suspend fun setReadAt(timestamp: Timetoken, vararg channelId: String)
}
