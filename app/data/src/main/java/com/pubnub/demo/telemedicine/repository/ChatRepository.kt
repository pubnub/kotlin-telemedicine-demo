package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.chat.ChatData
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun get(id: String): ChatData?
    fun getAll(userId: String): Flow<List<ChatData>>
    fun getPatientChats(userId: String): Flow<List<ChatData>>
    fun getDoctorChats(userId: String): Flow<List<ChatData>>
    suspend fun setReadAt(
        chatId: String,
        timestamp: Timetoken = System.currentTimeMillis() * 10_000L,
    )

    fun getUnreadCount(userId: String): Flow<Long>
}
