package com.pubnub.demo.telemedicine.network.service

import com.pubnub.api.models.consumer.objects.channel.PNChannelMetadata
import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.demo.telemedicine.network.util.IdMapper.userToChannelId
import com.pubnub.demo.telemedicine.network.util.asScalar
import com.pubnub.demo.telemedicine.network.util.toObject
import com.pubnub.demo.telemedicine.repository.ChannelRepository
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.flow.coroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
)
class ChannelService(
    private val channelRepository: ChannelRepository,
    private val channelGroupService: ChannelGroupService,
    private val userService: UserService,
    private val userRepository: UserRepository,
) {

    /**
     * Check is channel exists
     */
    suspend fun hasChannel(channelId: String): Boolean =
        channelRepository.get(channelId) != null

    /**
     * Get a channel from repository or synchronize it from server when it doesn't exists
     * @param channelId of channel to get
     */
    suspend fun getChannel(channelId: ChannelId): ChannelData? =
        channelRepository.get(channelId) ?: synchronizeChannel(channelId)

    suspend fun setReadAt(channelId: String) =
        channelRepository.setReadAt(channelId)

    /**
     * Pull the channel data from server and store it in database
     * @param channelId
     *
     * @return [ChannelData] of synchronized channel or null, when an exception occurred
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun synchronizeChannel(channelId: ChannelId): ChannelData? {
        Timber.i("Synchronize channel data: '$channelId'")
        val channelResult = try {
            PubNubFramework.instance.getChannelMetadata(
                channel = channelId,
                includeCustom = true
            ).coroutine()
        } catch (e: Exception) {
            Timber.w(e)
            return null
        }

        val channel = channelResult.data?.toChannelData() ?: return null

        // insert or update channel
        channelRepository.add(channel)

        // synchronize user accounts
        val users = mutableListOf<UserId>().apply {
            channel.metadata?.doctors?.run { addAll(this) }
            channel.metadata?.patient?.run { add(this) }
        }.toTypedArray()

        // TODO: 12/4/20 decide when to synchronize users (channel groups might change)
        //userService.synchronize(*users)
        userService.synchronizeNew(*users)

        return channel
    }

    /**
     * Returns user channel
     * @return [ChannelData] or null, when it doesn't exists
     */
    suspend fun getUserChannel(userId: UserId = PubNubFramework.userId): ChannelData? {
        val channelId = userId.userToChannelId()
        return channelRepository.get(channelId)
    }

    /**
     * Returns flow of user channel groups to subscribe
     */
    fun getUserChannelGroupIds(userId: String = PubNubFramework.userId): Flow<List<String>> {
        val user = runBlocking { userRepository.getUserByUuid(userId) }!!
        return (if (user.isDoctor())
            channelRepository.getDoctorChannel(userId)
        else channelRepository.getPatientChannel(userId)
                )
            .filterNotNull()
            .distinctUntilChangedBy { it.metadata?.groups }
            .mapNotNull { it.metadata?.groups }
    }

    // region Synchronization
    /**
     * Synchronize user channels and restore it in database
     *
     * @param userId of user or current user by default
     */
    internal suspend fun synchronize(userId: UserId = PubNubFramework.userId) {
        val userChannel = synchronizeUserChannel(userId)

        if (userChannel == null) {
            Timber.e("User '$userId' channel doesn't exists!")
            return
        }

        val channels = getAllChannels(userChannel)

        Timber.i("User channels: ${channels.joinToString(", ")}")
        synchronizeChannels(*channels.toTypedArray())
    }

    /**
     * Synchronize user channel
     * @see synchronizeChannel
     */
    private suspend fun synchronizeUserChannel(userId: UserId): ChannelData? {
        Timber.i("Synchronize user channel")

        // get user channel first
        val channelName = userId.userToChannelId()
        val userChannel = synchronizeChannel(channelName)

        Timber.i("User channel synchronized: $userChannel")
        return userChannel
    }

    /**
     * Get list of all channels user is assigned to
     * @param userChannel object (main user channel)
     * @return list of channel id
     */
    private suspend fun getAllChannels(userChannel: ChannelData): List<String> {
        Timber.i("Get all user channels")
        // get channels from user channel groups
        return userChannel.metadata?.groups?.flatMap { groupId ->

            Timber.i("Channel group: $groupId")
            // get channels from channel group and store it in db
            val channelIds = channelGroupService.getChannelIds(groupId)
            channelGroupService.add(groupId, channelIds)

            Timber.i("Channels: ${channelIds.joinToString(", ")}")
            channelIds
        }?.distinct() ?: emptyList()
    }

    /**
     * Synchronize channels
     * @see synchronizeChannel
     */
    internal suspend fun synchronizeChannels(vararg channel: ChannelId) {
        Timber.i("Synchronize channels")
        // get channels data
        channel.forEachIndexed { index, id ->
            Timber.i("[${index + 1}/${channel.size}] Channel $id")
            try {
                synchronizeChannel(id)
            } catch (e: Exception) {
                Timber.e(e, "Cannot synchronize channel $id")
            }
        }
        Timber.i("Channels synchronized")
    }
    // endregion

    fun PNChannelMetadata.toChannelData(): ChannelData =
        ChannelData(
            id = id,
            name = name ?: "",
            metadata = custom?.asScalar()?.toObject(),
        )
}
