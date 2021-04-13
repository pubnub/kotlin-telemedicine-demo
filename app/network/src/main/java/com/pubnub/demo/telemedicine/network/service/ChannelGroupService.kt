package com.pubnub.demo.telemedicine.network.service

import com.pubnub.api.PubNubException
import com.pubnub.demo.telemedicine.data.channel.ChannelGroupData
import com.pubnub.demo.telemedicine.repository.ChannelGroupRepository
import com.pubnub.demo.telemedicine.repository.ChannelRepository
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.util.flow.single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

class ChannelGroupService(
    private val channelGroupRepository: ChannelGroupRepository,
    private val channelRepository: ChannelRepository,
) {

    /**
     * Get list of channel id from selected channel group
     * @param channelGroupId to list channels
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Throws(PubNubException::class)
    suspend fun getChannelIds(channelGroupId: String): List<String> =
        PubNubFramework.instance.listChannelsForChannelGroup(channelGroupId)
            .single().channels

    /**
     * Check is channel added to group
     * @param channelGroupId to add channel to
     * @param channelId to add to group
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Throws(PubNubException::class)
    suspend fun hasChannel(channelGroupId: String, channelId: String): Boolean =
        getChannelIds(channelGroupId).any { it == channelId }

    /**
     * Add channels to channel group
     * If group is not exists it will be created
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun addChannels(channelGroupId: String, vararg channelId: String) {

        val group = getOrCreateGroup(channelGroupId)

        Timber.e("Add channels ${channelId.joinToString(", ")} to channel group ${group.id}")
        PubNubFramework.instance.addChannelsToChannelGroup(channelId.toList(), group.id)
            .single()

        Timber.e("Channels added")

        channelId.forEach {
            channelGroupRepository.addChannelToGroup(it, group)
        }
    }

    /**
     * Add or update channels in group
     */
    suspend fun add(groupId: String, channelIds: List<String>) {
        val channelGroupData = ChannelGroupData(groupId, channelIds)
        if (channelGroupRepository.get(groupId) != null)
            channelGroupRepository.update(channelGroupData)
        else channelGroupRepository.add(channelGroupData)
    }

    /**
     * Remove channel from channel group
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun removeChannel(channelGroupId: String, vararg channelId: String) {
        PubNubFramework.instance.removeChannelsFromChannelGroup(channelId.toList(), channelGroupId)
            .single()
    }

    /**
     * Get exists or create new group
     */
    private suspend fun getOrCreateGroup(channelGroupId: String): ChannelGroupData {
        // check is group with empty space exists
        val group = channelRepository.get(channelGroupId)!!
        var destinationGroup = channelGroupRepository.getDestinationChannelGroup(group)

        // just create a new group and update channel
        if (destinationGroup == null) {
            var metadata = group.metadata
            val groups =
                metadata?.groups?.mapNotNull { channelGroupRepository.get(it) } ?: emptyList()

            destinationGroup =
                channelGroupRepository.createDestinationChannelGroup().also { newGroup ->
                    val newGroups = groups.map { it.id } + newGroup.id
                    metadata = metadata!!.copy(groups = newGroups)
                }

            // store channel with updated metadata
            channelRepository.add(group.copy(metadata = metadata))
        }

        return destinationGroup
    }
}
