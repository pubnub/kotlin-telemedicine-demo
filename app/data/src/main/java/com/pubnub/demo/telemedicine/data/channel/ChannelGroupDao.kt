package com.pubnub.demo.telemedicine.data.channel

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ChannelGroupDao {

    @Query("SELECT * FROM `channel_group` WHERE id LIKE :groupId LIMIT 1")
    suspend fun get(groupId: String): ChannelGroupData?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg data: ChannelGroupData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg data: ChannelGroupData)

    @Delete
    fun delete(data: ChannelGroupData)

    @Query("DELETE FROM `channel_group` WHERE id LIKE :groupId")
    fun delete(groupId: String)
}
