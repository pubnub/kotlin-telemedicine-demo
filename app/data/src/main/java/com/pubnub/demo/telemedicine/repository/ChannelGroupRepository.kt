package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.demo.telemedicine.data.channel.ChannelGroupData
import com.pubnub.demo.telemedicine.util.uuid
import kotlin.random.Random

interface ChannelGroupRepository {
    suspend fun get(id: String): ChannelGroupData?
    suspend fun add(group: ChannelGroupData): ChannelGroupData
    suspend fun add(name: String = Random.uuid("group")): ChannelGroupData
    suspend fun update(group: ChannelGroupData): ChannelGroupData

    suspend fun remove(id: String)

    suspend fun addChannelToGroup(channelId: String, group: ChannelGroupData)
    suspend fun getDestinationChannelGroup(groupChannel: ChannelData): ChannelGroupData?
    suspend fun createDestinationChannelGroup(): ChannelGroupData
}
