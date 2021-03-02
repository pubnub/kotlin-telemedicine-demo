package com.pubnub.demo.telemedicine.mapper

import com.pubnub.demo.telemedicine.data.channel.ChannelGroupData
import com.pubnub.demo.telemedicine.initialization.data.ChannelGroup

/**
 * Mapper for transforming Channel Group object from UI to Db and vice versa
 */
object ChannelGroupMapper {

    fun ChannelGroup.toDb(): ChannelGroupData =
        ChannelGroupData(
            id = group,
            channels = channels,
        )

    fun ChannelGroupData.toUi(): ChannelGroup =
        ChannelGroup(
            group = id,
            channels = channels,
        )
}
