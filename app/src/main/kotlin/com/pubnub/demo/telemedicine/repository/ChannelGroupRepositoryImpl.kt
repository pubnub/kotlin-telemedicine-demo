package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.demo.telemedicine.data.channel.ChannelGroupDao
import com.pubnub.demo.telemedicine.data.channel.ChannelGroupData
import timber.log.Timber

class ChannelGroupRepositoryImpl(
    private val groupDao: ChannelGroupDao,
) : ChannelGroupRepository {

    companion object {
        const val CHANNEL_GROUP_MAX_CAPACITY = 2_000
    }

    override suspend fun get(id: String): ChannelGroupData? =
        groupDao.get(id)

    override suspend fun remove(id: String) {
        groupDao.delete(id)
    }

    override suspend fun add(group: ChannelGroupData): ChannelGroupData {
        groupDao.insert(group)
        return group
    }

    override suspend fun add(name: String): ChannelGroupData {
        val group = ChannelGroupData(name)
        return add(group)
    }

    override suspend fun update(group: ChannelGroupData): ChannelGroupData {
        groupDao.update(group)
        return group
    }

    override suspend fun getDestinationChannelGroup(groupChannel: ChannelData): ChannelGroupData? {
        val groups = groupChannel.metadata?.groups?.mapNotNull { get(it) } ?: emptyList()

        // iterate over groups and check for max capacity
        Timber.w("Groups size: ${groups.map { it.id to it.channels.size }}")
        return groups.firstOrNull { it.channels.size < CHANNEL_GROUP_MAX_CAPACITY }
    }

    override suspend fun createDestinationChannelGroup() = add()

    override suspend fun addChannelToGroup(channelId: String, group: ChannelGroupData) {

        Timber.i("Add channel $channelId\nto group $group")

        update(
            group.copy(
                channels = listOf(
                    *group.channels.toTypedArray(),
                    channelId,
                )
            )
        )
    }
}
