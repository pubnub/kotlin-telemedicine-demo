package com.pubnub.demo.telemedicine.mapper

import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.demo.telemedicine.initialization.data.Channel

/**
 * Mapper for transforming Channel object from UI to Db and vice versa
 */
object ChannelMapper {

    fun Channel.toDb(): ChannelData =
        ChannelData(
            id = id,
            name = name,
            metadata = metadata.toDb(),
        )

    fun ChannelData.toUi(): Channel =
        Channel(
            id = id,
            name = name,
            metadata = metadata.toUi(),
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

    private fun ChannelData.Metadata?.toUi(): Channel.Metadata? =
        if (this == null) null
        else Channel.Metadata(
            type = type,
            createdAt = createdAt,
            updatedAt = updatedAt,
            closedAt = closedAt,
            readAt = readAt,
            groups = groups,
            patient = patient,
            doctors = doctors,
        )
}
