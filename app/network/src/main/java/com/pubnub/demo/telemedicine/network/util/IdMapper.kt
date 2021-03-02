package com.pubnub.demo.telemedicine.network.util

import com.pubnub.framework.data.ChannelGroupId
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

object IdMapper {
    private const val user = "user"
    private const val channel = "channel"
    private const val group = "group"

    fun UserId.userToChannelId(): ChannelId = replace(user, channel)
    fun UserId.userToChannelGroupId(): ChannelGroupId = replace(user, group)

    fun ChannelId.channelToUserId(): UserId = replace(channel, user)
    fun ChannelId.channelToChannelGroupId(): ChannelGroupId = replace(channel, group)

    fun ChannelGroupId.groupToUserId(): UserId = replace(group, user)
    fun ChannelGroupId.groupToChannelId(): ChannelId = replace(group, channel)
}
