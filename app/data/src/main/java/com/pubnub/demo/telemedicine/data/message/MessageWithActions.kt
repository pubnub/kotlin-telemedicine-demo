package com.pubnub.demo.telemedicine.data.message

import androidx.room.Embedded
import androidx.room.PrimaryKey
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.timetoken

data class MessageWithActions(
    @PrimaryKey
    val id: String,

    val userId: String,

    val channelId: String,

    @Embedded(prefix = "custom_")
    val custom: MessageData.CustomData,

    val timestamp: Timetoken = System.currentTimeMillis().timetoken,

    val isSent: Boolean = true,

    var exception: String? = null,
) {
    fun toMessageData() =
        MessageData(id, userId, channelId, custom, timestamp, isSent, exception)
}
