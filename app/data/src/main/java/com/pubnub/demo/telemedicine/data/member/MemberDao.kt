package com.pubnub.demo.telemedicine.data.member

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Query("SELECT * FROM member WHERE channel LIKE :channelId LIMIT 1")
    fun get(channelId: String): MemberData?

    @Query("SELECT * FROM member")
    fun getAll(): Flow<List<MemberData>>

    @Query("SELECT * FROM member WHERE members LIKE '%' || :userId || '%'")
    fun getAll(userId: String): Flow<List<MemberData>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg member: MemberData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg member: MemberData)

    @Query("DELETE from member WHERE channel LIKE :channelId")
    suspend fun delete(channelId: String)

    @Delete
    suspend fun delete(member: MemberData)
}
