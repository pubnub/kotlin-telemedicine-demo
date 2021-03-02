package com.pubnub.demo.telemedicine.data.casestudy

import androidx.room.DatabaseView
import com.pubnub.demo.telemedicine.data.channel.ChannelType
import com.pubnub.framework.util.Timetoken

@DatabaseView("SELECT channel.id, channel.name, channel.custom_patient as patientId, channel.custom_doctors as doctors, channel.custom_closedAt as `closedAt`, channel.custom_createdAt as `createdAt`, (SELECT MAX(timestamp) FROM (SELECT timestamp FROM `message` WHERE channelId = channel.id UNION VALUES (channel.custom_updatedAt))) as `readAt`, (SELECT COUNT(id) FROM `message` WHERE channelId = channel.id and timestamp > channel.custom_readAt) as `unreadCount` FROM `channel` WHERE channel.custom_type = '${ChannelType.CASE}'")
data class CaseData(
    val id: String,
    val name: String,

    val doctors: List<String>,
    val patientId: String,
    val closedAt: Timetoken?,

    val createdAt: Timetoken,
    val readAt: Timetoken,
    val unreadCount: Long,
)
