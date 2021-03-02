package com.pubnub.demo.telemedicine.network.service

import com.pubnub.api.models.consumer.message_actions.PNMessageAction
import com.pubnub.demo.telemedicine.data.message.ReceiptData
import com.pubnub.demo.telemedicine.data.message.ReceiptTypeDef
import com.pubnub.demo.telemedicine.network.util.toJson
import com.pubnub.demo.telemedicine.repository.ReceiptRepository
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.ActionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
)
class ReceiptService(
    private val actionService: ActionService,
    private val receiptRepository: ReceiptRepository,
) {

    companion object {
        const val RECEIPT = "receipt"
    }

    private lateinit var actionJob: Job
    private val userId: UserId get() = PubNubFramework.userId

    /**
     * Start listening for Actions
     */
    fun bind() {
        Timber.e("Bind")
        listenForActions()
    }

    /**
     * Stop listening for Actions
     */
    fun unbind() {
        Timber.e("Unbind")
        if (::actionJob.isInitialized)
            stopListenForActions()
    }

    suspend fun isDelivered(channelId: ChannelId, messageTimestamp: Long, userId: UserId) =
        receiptRepository.isDelivered(channelId, messageTimestamp, userId)

    suspend fun isRead(channelId: ChannelId, messageTimestamp: Long, userId: UserId) =
        receiptRepository.isRead(channelId, messageTimestamp, userId)

    suspend fun setDelivered(channelId: ChannelId, messageTimestamp: Long) {
        setAction(channelId, messageTimestamp, ReceiptData.Type.DELIVERED)
    }

    suspend fun setRead(channelId: ChannelId, messageTimestamp: Long) {
        setAction(channelId, messageTimestamp, ReceiptData.Type.READ)
    }

    suspend fun getLastRead(channelId: ChannelId, userId: UserId) =
        receiptRepository.getLastRead(channelId, userId).mapNotNull { it?.actionTimestamp }

    suspend fun synchronize(channelId: ChannelId, lastTimestamp: Long? = null) {
        Timber.e("Sync receipts for channel '$channelId'")
        GlobalScope.launch(Dispatchers.IO) {
            val lastReactionTimestamp =
                lastTimestamp ?: receiptRepository.getLastTimestamp(channelId)
            Timber.e("\twith last timestamp '$lastReactionTimestamp'")
            val receipts = actionService.getAll(
                channelId = channelId,
                end = lastReactionTimestamp + 1,
                start = null,
                limit = 100,
            ).filter { it.type == RECEIPT }
                .map { action ->
                    ReceiptData(
                        action = action.value,
                        userId = action.uuid!!,
                        channelId = channelId,
                        messageTimestamp = action.messageTimetoken,
                        actionTimestamp = action.actionTimetoken!!
                    )
                }
            receiptRepository.insertUpdate(*receipts.toTypedArray())
        }
    }

    private suspend fun setAction(
        channelId: ChannelId,
        messageTimestamp: Long,
        @ReceiptTypeDef action: String,
    ) {
        Timber.e("Set message as '$action', channel '$channelId' with messageTimestamp '$messageTimestamp'")
        val result =
            actionService.add(channelId, PNMessageAction(RECEIPT, action, messageTimestamp))
        process(result, channelId, userId)
    }

    /**
     * Listen for incoming Actions and process it
     * @see process
     */
    private fun listenForActions() {
        GlobalScope.launch(Dispatchers.IO) {
            actionJob = actionService.actions
                .filter { it.messageAction.type == RECEIPT }
                .filter { it.publisher != PubNubFramework.userId }
                .onEach {
                    process(it.messageAction, it.channel, it.publisher!!)
                }
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
     * Store incoming message receipt
     *
     * @param action
     * @param userId invited to channel
     * @param channelId user is invited to
     */
    private suspend fun process(
        action: PNMessageAction,
        channelId: ChannelId,
        userId: UserId,
    ) {
        Timber.i("Process receipt to channel '$channelId': ${action.toJson()}")

        val receipt = ReceiptData(
            action = action.value,
            userId = userId,
            channelId = channelId,
            messageTimestamp = action.messageTimetoken,
            actionTimestamp = action.actionTimetoken!!
        )

        receiptRepository.insertUpdate(receipt)
    }
}