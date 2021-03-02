package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.casestudy.CaseData
import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

interface CaseRepository {
    suspend fun get(id: String): CaseData?
    suspend fun getLastForPatient(userId: UserId): CaseData?
    suspend fun add(
        channelData: ChannelData,
        patientId: String,
        vararg doctorId: String,
    ): CaseData

    fun getAll(userId: String): Flow<List<CaseData>>
    suspend fun setReadAt(
        caseId: String,
        timestamp: Timetoken = System.currentTimeMillis() * 10_000L,
    )

    fun getUnreadCount(userId: String): Flow<Long>
}
