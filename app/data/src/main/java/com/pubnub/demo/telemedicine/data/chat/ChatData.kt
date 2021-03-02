package com.pubnub.demo.telemedicine.data.chat

import androidx.room.DatabaseView
import com.pubnub.demo.telemedicine.data.channel.ChannelType

@DatabaseView("SELECT channel.id, channel.name, channel.custom_patient as `patient`, channel.custom_doctors as doctors, (SELECT MAX(timestamp) FROM (SELECT timestamp FROM `message` WHERE channelId = channel.id UNION VALUES (channel.custom_updatedAt))) as `readAt`, (SELECT COUNT(id) FROM `message` WHERE channelId = channel.id and timestamp > channel.custom_readAt) as `unreadCount` FROM `channel` WHERE channel.custom_type = '${ChannelType.CHAT}'")
data class ChatData(
    val id: String,
    val name: String,

    val doctors: List<String>,
    val patient: String?,

    val readAt: com.pubnub.framework.util.Timetoken,
    val unreadCount: Long,
)
