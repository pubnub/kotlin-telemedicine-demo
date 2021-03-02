package com.pubnub.framework.service

import com.pubnub.api.models.consumer.PNBoundedPage
import com.pubnub.api.models.consumer.message_actions.PNAddMessageActionResult
import com.pubnub.api.models.consumer.message_actions.PNGetMessageActionsResult
import com.pubnub.api.models.consumer.message_actions.PNMessageAction
import com.pubnub.api.models.consumer.message_actions.PNRemoveMessageActionResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Framework
import com.pubnub.util.flow.event
import com.pubnub.util.flow.messageAction
import com.pubnub.util.flow.single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Action Service which handle [PNMessageActionResult]
 *
 * Used to send / receive channel invitations
 */
@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
@Framework
class ActionService {

    private val _actions: BroadcastChannel<PNMessageActionResult> =
        BroadcastChannel(Channel.BUFFERED)
    val actions: Flow<PNMessageActionResult> get() = _actions.asFlow()

    private lateinit var actionJob: Job

    init {
        bind()
    }

    /**
     * Send message action
     *
     * @param channelId you invite to
     * @param messageAction to send
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun add(
        channelId: ChannelId,
        messageAction: PNMessageAction,
    ): PNAddMessageActionResult {
        Timber.i("Send message action to channel '$channelId': $messageAction")
        return PubNubFramework.instance.addMessageAction(
            channel = channelId,
            messageAction = messageAction,
        ).single()
    }

    /**
     * Remove message action
     *
     * @param channelId Channel to remove message actions from.
     * @param messageTimetoken The publish timetoken of the original message.
     * @param actionTimetoken The publish timetoken of the message action to be removed.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun remove(
        channelId: ChannelId,
        messageTimetoken: Long,
        actionTimetoken: Long,
    ): PNRemoveMessageActionResult {
        Timber.i("Remove message action from channel '$channelId', messageTimetoken '$messageTimetoken', actionTimetoken '$actionTimetoken'")
        return PubNubFramework.instance.removeMessageAction(
            channel = channelId,
            messageTimetoken = messageTimetoken,
            actionTimetoken = actionTimetoken
        ).single()
    }

    /**
     * Get message action
     *
     * @param channelId Channel to fetch message actions from.
     * @param start Message Action timetoken denoting the start of the range requested
     *              (return values will be less than start).
     * @param end Message Action timetoken denoting the end of the range requested
     *            (return values will be greater than or equal to end).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun get(
        channelId: ChannelId,
        start: Long?,
        end: Long?,
        limit: Int? = null,
    ): PNGetMessageActionsResult {
        Timber.i("Get message action from channel '$channelId', time [$end : $start)")
        return PubNubFramework.instance.getMessageActions(
            channel = channelId,
            page = PNBoundedPage(start = start,
                end = end,
                limit = limit)).single()
    }


    /**
     * Synchronize message actions
     *
     * @param channelId Channel to fetch message actions from.
     * @param start Message Action timetoken denoting the start of the range requested
     *              (return values will be less than start).
     * @param end Message Action timetoken denoting the end of the range requested
     *            (return values will be greater than or equal to end).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getAll(
        channelId: ChannelId,
        start: Long?,
        end: Long?,
        limit: Int?,
    ): List<PNMessageAction> =
        getAll(channelId, start, end, limit, mutableListOf())

    // TODO: 12/10/20 result might be changed into flow
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getAll(
        channelId: ChannelId,
        start: Long?,
        end: Long?,
        limit: Int?,
        results: MutableList<PNMessageAction>,
    ): List<PNMessageAction> {

        val result = get(channelId, start, end, limit)
        val newActions = result.actions
        results.addAll(newActions)

        Timber.e("Sync successful. New actions: ${newActions.size}")
        return when {
            result.page != null -> {
                Timber.i("Page: ${result.page}")
                val newestActionTimestamp = result.page!!.start

                Timber.e("Trying to sync successful with end '$newestActionTimestamp'")
                getAll(channelId, newestActionTimestamp, end, limit, results)
            }
            newActions.isNotEmpty() -> {
                val newestActionTimestamp = newActions.minOf { it.actionTimetoken!! }
                Timber.e("Trying to sync successful with end '$newestActionTimestamp'")
                getAll(channelId, newestActionTimestamp, end, limit, results)
            }
            else -> {
                Timber.e("Sync successful. No more actions. Result size: ${results.size}")
                results
            }
        }
    }

    /**
     * Start listening for Actions
     */
    private fun bind() {
        Timber.e("Bind")
        listenForActions()
    }

    /**
     * Stop listening for Actions
     */
    private fun unbind() {
        Timber.e("Unbind")
        if (::actionJob.isInitialized)
            stopListenForActions()
    }

    /**
     * Listen for incoming Actions and process it
     * @see processAction
     */
    private fun listenForActions() {
        GlobalScope.launch(Dispatchers.IO) {
            actionJob = PubNubFramework.instance.event.messageAction
                .onEach { it.processAction() }
                .launchIn(this)
        }
    }

    /**
     * Cancel incoming Actions listener
     */
    private fun stopListenForActions() {
        actionJob.cancel()
    }

    /**
     * Process Actions
     *
     * Will check [PNMessageActionResult.messageAction] type and process it.
     * Accepted types:
     * - [INVITATION] - invitation to channel
     */
    private suspend fun PNMessageActionResult.processAction() {
        Timber.i("Action received: $messageAction")
        _actions.send(this@processAction)
    }

    enum class Type(val value: String) {
        EVENT_ADDED("added"),
        EVENT_REMOVED("removed"),
    }
}
