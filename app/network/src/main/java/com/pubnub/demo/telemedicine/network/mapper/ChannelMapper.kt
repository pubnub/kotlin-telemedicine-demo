package com.pubnub.demo.telemedicine.network.mapper

import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.demo.telemedicine.initialization.data.Channel

internal object ChannelMapper {

    fun Channel.toDb(): ChannelData =
        ChannelData(
            id = id,
            name = name,
            metadata = metadata.toDb()
        )

    private fun Channel.Metadata?.toDb(): ChannelData.Metadata? =
        if (this == null) null
        else ChannelData.Metadata(
            type = type,
            createdAt = createdAt,
            updatedAt = updatedAt,
            closedAt = closedAt,
            readAt = readAt,
            groups = groups ?: emptyList(),
            patient = patient,
            doctors = doctors ?: emptyList(),
        )
}
