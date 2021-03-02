package com.pubnub.demo.telemedicine.data.message

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.timetoken
import java.util.UUID

@Keep
@Entity(tableName = "message")
data class MessageData(
    @PrimaryKey
    val id: String,

    val userId: String,

    val channelId: String,

    @Embedded(prefix = "custom_")
    val custom: CustomData,

    val timestamp: Timetoken = System.currentTimeMillis().timetoken,

    val isSent: Boolean = true,

    var exception: String? = null,
) {
    @Keep
    @Entity(tableName = "message_custom")
    data class CustomData(
        @PrimaryKey
        val id: String = UUID.randomUUID().toString(),

        val content: String,

        val image: String? = null,
    )
}
