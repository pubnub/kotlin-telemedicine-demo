package com.pubnub.framework.service

import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.Typing
import com.pubnub.framework.data.TypingMap
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.UserIdMapper
import com.pubnub.framework.util.Framework
import com.pubnub.framework.util.Seconds
import com.pubnub.framework.util.TypingIndicator
import com.pubnub.framework.util.timetoken
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Service to send and collect data about typing users
 *
 * Received data will be mapped by [UserIdMapper]
 *
 * @param occupancyService will listen for typing user disconnection
 * @param userIdMapper for mapping uuid into name
 */
@ObsoleteCoroutinesApi
@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
@Framework
class TypingService(
    private val occupancyService: OccupancyService,
    private val userIdMapper: UserIdMapper,
) {

    companion object {
        private const val TIMEOUT: Seconds = 5_000L
        private const val SEND_TIMEOUT: Seconds = 3_000L
    }

    private val _typing: ConflatedBroadcastChannel<TypingMap> =
        ConflatedBroadcastChannel(hashMapOf())
    private val typing: Flow<List<Typing>>
        get() = _typing.asFlow()
            .map { it.values.toList() }
            .debounce(1_000L)
            .distinctUntilChanged()


    private var signalJob: Job? = null
    private var typingTimeoutJob: Job? = null
    private lateinit var typingOccupancy: Deferred<Unit>
    private var timer: ReceiveChannel<Unit>? = null

    // region Typing
    /**
     * Listen for typing on provided channel
     */
    fun typing(channelId: ChannelId): Flow<List<Typing>> =
        typing.map { list ->
            Timber.e("Typing for $channelId: \n$list")
            list.filter { data ->
                data.channelId == channelId // is current channel
                        && data.userId != PubNubFramework.userId // user is not own
            }.map {
                val userName = userIdMapper.map(it.userId)
                it.copy(userId = userName)
            }
        }

    /**
     * Set typing state for passed user and channel ID
     */
    suspend fun setTyping(userId: UserId, channelId: ChannelId, isTyping: Boolean) {
        val data = Typing(userId, channelId, isTyping)
        if (shouldSendTypingEvent(data))
            TypingIndicator.setTyping(
                channelId = channelId,
                isTyping = isTyping,
                onError = { Timber.e("Cannot send typing signal\n$it") }
            )
    }
    // endregion

    // Binding
    /**
     * Bind for signals and launch timeout timer
     */
    fun bind(channelId: ChannelId) {
        Timber.i("-> Bind for typing signal: $channelId ($this)")
        listenForSignal()
        startTimeoutTimer()
        listenForOccupancy(channelId)
    }

    /**
     * Unbind signal changes listener and stop timeout timer
     */
    fun unbind() {
        Timber.i("<- Unbind for typing signal ($this)")
        stopListenForPresence()
        stopTimeoutTimer()
        stopListenForOccupancy()
    }

    /**
     * Listen for incoming signal and process it
     */
    private fun listenForSignal() {
        if (signalJob != null) return
        GlobalScope.launch(Dispatchers.IO) {
            signalJob = TypingIndicator.getTyping()
                .onEach { setTypingData(it) }
                .launchIn(this)
        }
    }

    /**
     * Cancel incoming signal listener
     */
    private fun stopListenForPresence() {
        if (signalJob != null) {
            signalJob!!.cancel()
            signalJob = null
        }
    }

    /**
     * Listen for occupancy to change typing state after timeout
     */
    private fun listenForOccupancy(channelId: ChannelId) {

        // expect that we're already bound to service
        typingOccupancy = GlobalScope.async {
            typing.map { list -> list.filter { it.isTyping } }
                .combine(occupancyService.getOccupancy(channelId)) { typingList, occupancy ->
                    typingList.forEach { typing ->
                        if (typing.userId !in occupancy.list ?: listOf())
                            setTypingData(typing.userId, channelId, false)
                    }
                }
                .launchIn(this)
        }
    }

    /**
     * Stop listen for occupancy changes
     */
    private fun stopListenForOccupancy() {
        if (::typingOccupancy.isInitialized)
            typingOccupancy.cancel()
    }
    // endregion

    // region Timer
    /**
     * Start timer which will check for outdated items every 1s
     * @see [removeOutdated]
     */
    private fun startTimeoutTimer() {
        if (timer != null) return
        timer = ticker(1_000L)
        GlobalScope.launch(Dispatchers.IO) {
            typingTimeoutJob = timer!!.receiveAsFlow()
                .onEach { removeOutdated() }
                .launchIn(this)
        }
    }

    /**
     * Cancel running timer and typing timeout flow
     */
    private fun stopTimeoutTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null

            typingTimeoutJob!!.cancel()
        }
    }
    // endregion

    // region Typing event
    /**
     * Check if new event should be send
     */
    private fun shouldSendTypingEvent(state: Typing): Boolean {
        synchronized(_typing) {
            val typingMap = _typing.value
            val shouldSendEvent =
                (!typingMap.containsKey(state.userId) // check is no event with this userId
                        || typingMap[state.userId]!!.isTyping != state.isTyping // state has changed
                        || typingMap[state.userId]!!.shouldResend  // timeout occurred
                        )
            if (shouldSendEvent) typingMap[state.userId] = state
            return shouldSendEvent
        }
    }

    /**
     * Remove outdated typing events
     * @see Typing.isOutdated
     */
    private fun removeOutdated() {
        synchronized(_typing) {
            val typingMap = _typing.value
            val outdated = typingMap.filterValues { it.isOutdated }
            outdated.values.toList().forEach {
                setTypingData(it.userId, it.channelId, false)
            }
        }
    }

    /**
     * Set new typing state for passed user and channel
     * Will produce new channel item
     */
    private fun setTypingData(typingData: Typing) {
        synchronized(_typing) {
            val newList = _typing.value

            // remove previous data first
            newList.remove(typingData.userId)

            // set new data
            if (typingData.isTyping)
                newList[typingData.userId] = typingData

            _typing.offer(newList)
        }
    }

    private fun setTypingData(
        userId: UserId,
        channelId: ChannelId,
        isTyping: Boolean,
        timestamp: Long = System.currentTimeMillis().timetoken,
    ) =
        setTypingData(Typing(userId, channelId, isTyping, timestamp))

    // endregion

    private val Typing.isOutdated: Boolean
        get() = timestamp + TIMEOUT.timetoken <= System.currentTimeMillis().timetoken

    private val Typing.shouldResend: Boolean
        get() = timestamp + SEND_TIMEOUT.timetoken <= System.currentTimeMillis().timetoken
}

