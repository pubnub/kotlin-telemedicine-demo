package com.pubnub.demo.telemedicine.network.service

import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.demo.telemedicine.initialization.data.Signal
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.flow.event
import com.pubnub.framework.util.flow.signal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * Synchronization Service for synchronize data between server and local database
 */
@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
)
class SynchronizationService(
    private val channelService: ChannelService,
    private val userService: UserService,
) {
    private lateinit var result: Deferred<Unit>
    private lateinit var signalJob: Job

    private val _running: Channel<Boolean> = Channel()

    init {
        listenForSignal()
    }

    /**
     * Start synchronization
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun start() {
        Timber.i("Synchronization start called")
        GlobalScope.launch(Dispatchers.IO) {
            if (runBlocking { !_running.isEmpty && _running.receive() }) {
                Timber.w("Cannot start synchronization. Previous call is not ended.")
                return@launch
            }
            result = GlobalScope.async(Dispatchers.IO) {
                Timber.i("Synchronization started")
                setProgress(true)

                channelService.synchronize()

                setProgress(false)
                Timber.i("Synchronization finished")
            }
        }
    }

    fun synchronizeChannels(vararg channelId: ChannelId) {
        GlobalScope.launch(Dispatchers.IO) {
            channelService.synchronizeChannels(*channelId)
        }
    }

    fun synchronizeUsers(vararg userId: UserId) {
        GlobalScope.launch(Dispatchers.IO) {
            userService.synchronize(*userId)
        }
    }

    /**
     * Stop synchronization
     */
    fun stop() {
        if (::result.isInitialized)
            result.cancel()
        GlobalScope.launch(Dispatchers.IO) { setProgress(false) }
    }

    private fun getSignal(): Flow<Signal> =
        PubNubFramework.instance.event.signal
            .filter { it.isSyncEvent }
            .map { it.toSyncSignal() }

    /**
     * Send information about current progress
     */
    private fun CoroutineScope.setProgress(isRunning: Boolean) =
        launch { _running.send(isRunning) }

    private fun listenForSignal() {
        if (::signalJob.isInitialized) return

        GlobalScope.launch(Dispatchers.IO) {
            signalJob = getSignal()
                .onEach { processSignal(it) }
                .launchIn(this)
        }

    }

    private fun stopListenForSignal() {
        if (::signalJob.isInitialized)
            signalJob.cancel()
    }

    private fun processSignal(signal: Signal) {
        GlobalScope.launch(Dispatchers.IO) {
            when (signal.type) {
                Signal.Type.CHANNEL -> {
                    channelService.synchronizeChannel(signal.value)
                }
                Signal.Type.USER -> {
                    userService.synchronize(signal.value)
                }
            }
        }
    }

    private val PNSignalResult.isSyncEvent: Boolean
        get() = try {
            message.isJsonArray && message.asJsonArray[0].asString in arrayOf(Signal.Type.CHANNEL.name,
                Signal.Type.USER.name)
        } catch (e: Exception) {
            false
        }

    private fun PNSignalResult.toSyncSignal(): Signal = with(message.asJsonArray) {
        Signal(Signal.Type.valueOf(get(0).asString), get(1).asString)
    }
}
