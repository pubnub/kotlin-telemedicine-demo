package com.pubnub.demo.telemedicine.data.casestudy

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {

    @Query("SELECT * FROM CaseData WHERE id = :caseId LIMIT 1")
    fun get(caseId: String): Flow<CaseData?>

    @Query("SELECT * FROM CaseData WHERE patientId = :userId ORDER BY createdAt DESC LIMIT 1")
    fun getLastByPatient(userId: String): Flow<CaseData?>

    @Query("SELECT * FROM CaseData WHERE doctors LIKE '%' || :doctorId || '%' ORDER BY readAt DESC")
    fun getAll(doctorId: String): Flow<List<CaseData>>

    @Query("SELECT * FROM CaseData WHERE doctors LIKE '%' || :doctorId || '%' ORDER BY readAt DESC LIMIT :limit")
    fun getAll(doctorId: String, limit: Int): Flow<List<CaseData>>

    @Query("UPDATE `channel` SET custom_readAt = :timestamp WHERE id IN (:caseId)")
    fun setReadAt(timestamp: com.pubnub.framework.util.Timetoken, vararg caseId: String)

    @Query("SELECT SUM(unreadCount) FROM CaseData WHERE doctors LIKE '%' || :doctorId || '%'")
    fun getUnreadCount(doctorId: String): Flow<Long?>
}
