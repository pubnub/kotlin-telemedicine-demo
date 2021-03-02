package com.pubnub.framework.service

import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.Occupancy
import com.pubnub.framework.data.OccupancyMap
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.OccupancyMapper
import com.pubnub.framework.util.Framework
import com.pubnub.util.flow.coroutine
import com.pubnub.util.flow.event
import com.pubnub.util.flow.presence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Occupancy Service responsible for notifying about channels occupation changes
 */
@FlowPreview
@Framework
@OptIn(ExperimentalCoroutinesApi::class)
class OccupancyService(private val occupancyMapper: OccupancyMapper) {

    // TODO: 10/19/20 state flow is not working well yet, setting a value looks unstable (check member list)
    private val _occupancy: ConflatedBroadcastChannel<OccupancyMap> =
        ConflatedBroadcastChannel(OccupancyMap())
    val occupancy: Flow<OccupancyMap>
        get() = _occupancy.asFlow()

    private var channels: MutableSet<String> = mutableSetOf()
    private var channelGroups: MutableSet<String> = mutableSetOf()

    private lateinit var presenceJob: Job

    private val bound: AtomicBoolean = AtomicBoolean(false)

    // region Channels
    /**
     * Set channels to subscribe
     * Will override previous channels
     */
    fun setChannels(vararg channel: String) {
        if (channels.equals(channel)) return
        resubscribe {
            channels = mutableSetOf(*channel)
        }
    }

    /**
     * Add channel to subscribe
     * Will add passed channel to previous ones
     */
    fun addChannel(channel: String) {
        if (channels.contains(channel)) return
        resubscribe {
            channels.add(channel)
        }
    }

    /**
     * Remove all channels and channel groups from subscription
     */
    fun removeAll() {
        if (bound.get()) unbind()
        channels = mutableSetOf()
        channelGroups = mutableSetOf()
    }

    /**
     * Remove channel from subscription
     * Will remove channel from channel list to subscribe
     */
    fun removeChannel(channel: String) {
        if (!channels.contains(channel)) return
        resubscribe {
            channels.remove(channel)
        }
    }

    /**
     * Remove all channels from subscription
     */
    fun removeAllChannels() {
        if (bound.get()) unbind()
        channels = mutableSetOf()
    }
    // endregion

    // region Channel Groups
    /**
     * Set channel groups to subscribe
     * Will override previous channel groups
     */
    fun setChannelGroups(vararg channelGroup: String) {
        // TODO: 12/2/20 change this check to accept channel added to channelGroup
        if (channelGroups.equals(channelGroup)) return
        resubscribe {
            channelGroups = mutableSetOf(*channelGroup)
        }
    }

    /**
     * Add channel group to subscribe
     * Will add passed channel group to previous ones
     */
    fun addChannelGroup(channelGroup: String) {
        if (channelGroups.contains(channelGroup)) return
        resubscribe {
            channelGroups.add(channelGroup)
        }
    }

    /**
     * Remove channel group from subscription
     * Will remove channel group from channel group list to subscribe
     */
    fun removeChannelGroup(channelGroup: String) {
        if (!channelGroups.contains(channelGroup)) return
        resubscribe {
            channelGroups.remove(channelGroup)
        }
    }

    /**
     * Remove all channel groups from subscription
     */
    fun removeAllChannelGroups() {
        if (bound.get()) unbind()
        channelGroups = mutableSetOf()
    }
    // endregion

    /**
     * Returns occupancy for passed channel
     * @param channelId for occupancy check
     */
    fun getOccupancy(channelId: String): Flow<Occupancy> =
        occupancy.map { it[channelId] ?: Occupancy(channelId, 0, emptyList()) }

    fun isOnline(userId: UserId): Flow<Boolean> =
        occupancy.map { map -> map.any { it.value.list?.contains(userId) ?: false } }

    fun getOnline(): Flow<List<UserId>> =
        occupancy.map { map -> map.flatMap { it.value.list ?: listOf() }.distinct() }

    // region Binding
    private fun bind() {
        Timber.e("Bind")
        //initCallbacks()
        listenForPresence()
        joinLobby()
        bound.set(true)
    }

    private fun unbind() {
        // unsubscribe
        if (::presenceJob.isInitialized)
            stopListenForPresence()
        leaveChannels()
        bound.set(false)
    }
    // endregion

    private fun resubscribe(block: () -> Unit) {
        if (bound.get()) unbind()
        block.invoke()
        if (channels.isNotEmpty() || channelGroups.isNotEmpty()) bind()
    }

    /**
     * Listen for incoming presence and process it
     */
    private fun listenForPresence() {
        GlobalScope.launch(Dispatchers.IO) {
            presenceJob = PubNubFramework.instance.event.presence
                .onEach { it.processAction() }
                .launchIn(this)
        }
    }

    /**
     * Cancel incoming presence listener
     */
    private fun stopListenForPresence() {
        presenceJob.cancel()
    }

    private fun joinLobby() {
        Timber.w("Join channels: $channels")
        Timber.w("Join channelGroups: $channelGroups")

        if (channels.isEmpty() && channelGroups.isEmpty()) {
            Timber.e("No channels and not channelGroups provided")
            return
        }

        // Listen on Lobby
        PubNubFramework.instance.subscribe(
            channels = channels.toList(),
            channelGroups = channelGroups.toList(),
            withPresence = true
        )

        // Get lobby occupancy
        GlobalScope.launch(Dispatchers.IO) {
            try {
                getOccupancy(channels.toList(), channelGroups.toList())?.let { setOccupancy(it) }
            } catch (e: Exception) {
                Timber.w(e, "Cannot set occupancy")
            }
        }
    }

    private fun leaveChannels() {
        Timber.w("Leave channels: $channels")
        Timber.w("Leave channelGroups: $channelGroups")

        if (channels.isEmpty() && channelGroups.isEmpty()) {
            Timber.e("No channels and not channelGroups provided")
            Timber.e("No channels provided")
            return
        }

        // Listen on Lobby
        PubNubFramework.instance.unsubscribe(
            channels = channels.toList(),
            channelGroups = channelGroups.toList(),
        )
    }

    private suspend fun getOccupancy(
        channels: List<String>,
        channelGroups: List<String>,
    ): OccupancyMap? =
        PubNubFramework.instance.hereNow(
            channels = channels,
            channelGroups = channelGroups,
            includeUUIDs = true
        )
            .coroutine()
            .let { result ->
                occupancyMapper.map(result.channels)
            }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setOccupancy(occupancy: OccupancyMap) {
        Timber.i("Set occupancy: $occupancy")
        _occupancy.offer(occupancy)
    }

    private fun PNPresenceEventResult.processAction() {
        val occupancyMap = _occupancy.value
        val previousOccupants = occupancyMap[channel]
        val occupants = getOccupants(previousOccupants)

        val newOccupancy = Occupancy.from(this, occupants) ?: return
        val map = occupancyMap.apply {
            put(channel!!, newOccupancy)
        }
        setOccupancy(map)
    }

    private fun PNPresenceEventResult.getOccupants(occupancy: Occupancy?): List<String> =
        (occupancy?.list ?: listOf()).toMutableList().apply {
            when (event) {
                "join" -> add(uuid!!)
                "interval" -> {
                    leave?.let { removeAll { uuid -> uuid in it } }
                    timeout?.let { removeAll { uuid -> uuid in it } }
                    join?.let { addAll(it) }
                }
                "leave", "timeout" -> remove(uuid!!)
            }
        }
}
