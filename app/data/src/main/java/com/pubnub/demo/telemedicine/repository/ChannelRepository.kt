package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.demo.telemedicine.data.channel.ChannelTypeDef
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    suspend fun has(id: String): Boolean
    suspend fun get(id: String): ChannelData?
    suspend fun add(channel: ChannelData): ChannelData
    suspend fun remove(id: String)

    fun getByType(
        userId: UserId,
        @ChannelTypeDef type: String,
        filter: (ChannelData) -> Boolean = { true },
    ): Flow<List<ChannelData>>

    fun getDoctorChannel(doctorId: String): Flow<ChannelData?>
    fun getPatientChannel(patientId: String): Flow<ChannelData?>
    suspend fun getConversation(userId: UserId, contactId: UserId): ChannelData?

    suspend fun setReadAt(
        channelId: String,
        timestamp: Timetoken = System.currentTimeMillis() * 10_000L,
    )
}
