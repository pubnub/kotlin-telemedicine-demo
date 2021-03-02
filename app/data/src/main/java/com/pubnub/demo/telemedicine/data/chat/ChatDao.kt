package com.pubnub.demo.telemedicine.data.chat

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Query("SELECT * FROM ChatData WHERE id = :caseId LIMIT 1")
    fun get(caseId: String): Flow<ChatData?>

    @Query("SELECT * FROM ChatData WHERE patient LIKE :userId OR doctors LIKE '%' || :userId || '%' ORDER BY readAt DESC")
    fun getAll(userId: String): Flow<List<ChatData>>

    @Query("SELECT * FROM ChatData WHERE patient IS :userId AND doctors IS NOT null ORDER BY readAt DESC")
    fun getDoctorChats(userId: String): Flow<List<ChatData>>

    @Query("SELECT * FROM ChatData WHERE patient IS NOT NULL AND doctors LIKE '%' || :userId || '%' ORDER BY readAt DESC")
    fun getPatientChats(userId: String): Flow<List<ChatData>>

    @Query("SELECT * FROM ChatData WHERE patient LIKE :userId OR doctors LIKE '%' || :userId || '%' ORDER BY readAt DESC LIMIT :limit")
    fun getAll(userId: String, limit: Int): Flow<List<ChatData>>

    @Query("UPDATE `channel` SET custom_readAt = :timestamp WHERE id IN (:caseId)")
    fun setReadAt(timestamp: com.pubnub.framework.util.Timetoken, vararg caseId: String)

    @Query("SELECT SUM(unreadCount) FROM ChatData WHERE patient LIKE :userId OR doctors LIKE '%' || :userId || '%' ")
    fun getUnreadCount(userId: String): Flow<Long?>
}
