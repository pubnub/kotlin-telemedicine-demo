package com.pubnub.demo.telemedicine.data.channel

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pubnub.framework.util.timetoken
import java.util.UUID

@Keep
@Entity(tableName = "channel")
data class ChannelData(
    @PrimaryKey
    val id: String,

    val name: String,

    @Embedded(prefix = "custom_")
    val metadata: Metadata? = null,

    ) {

    @Keep
    @Entity(tableName = "channel_metadata")
    data class Metadata(
        @PrimaryKey
        val id: String = UUID.randomUUID().toString(),

        @ChannelTypeDef
        val type: String = ChannelType.CASE,

        val createdAt: Long = System.currentTimeMillis().timetoken,

        val updatedAt: Long = System.currentTimeMillis().timetoken,

        val closedAt: Long? = null,

        val readAt: Long = 0L,

        val groups: List<String> = emptyList(),

        val patient: String? = null,

        val doctors: List<String> = emptyList(),
    )
}
