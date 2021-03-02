package com.pubnub.demo.telemedicine.data.channel

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "channel_group")
data class ChannelGroupData(
    @PrimaryKey
    val id: String,

    val channels: List<String> = listOf(),
)
