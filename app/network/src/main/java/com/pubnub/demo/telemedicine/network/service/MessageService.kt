package com.pubnub.demo.telemedicine.network.service

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.demo.telemedicine.data.message.MessageData
import com.pubnub.demo.telemedicine.data.message.MessageWithActions
import com.pubnub.demo.telemedicine.network.paging.MessageRemoteMediator
import com.pubnub.demo.telemedicine.network.util.fromJson
import com.pubnub.demo.telemedicine.network.util.toJson
import com.pubnub.demo.telemedicine.repository.MessageRepository
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.flow.event
import com.pubnub.framework.util.flow.message
import com.pubnub.framework.util.flow.single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Message Service implementation
 *
 * The service is responsible for sending messages and communication with PubNub API.
 *
 * Binding for PubNub listener depends on [getAll] lifecycle.
 *
 * @param messageRepository to get or store messages in application
 * @param encryptionService to encrypt / decrypt message payload
 * @param messageMapper to map database message object ([MessageData]) into UI object ([T])
 * @param historyMapper to map PubNub history result object ([PNHistoryItemResult]) into
 *  database message object ([MessageData])
 */
@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
class MessageService<T : Any>(
    private val messageRepository: MessageRepository,
    private val channelService: ChannelService,
    private val messageMapper: Mapper<MessageWithActions, T>,
    private val historyMapper: Mapper<PNHistoryItemResult, MessageData>,
) {

    private lateinit var messageJob: Job

    private val bound: AtomicBoolean = AtomicBoolean(false)

    /**
     * Start listening for messages
     */
    fun bind() {
        Timber.e("Bind")
        listenForMessages()
        bound.set(true)
    }

    /**
     * Stop listening for messages
     */
    fun unbind() {
        Timber.e("Unbind")
        if (::messageJob.isInitialized)
            stopListenForMessages()
        bound.set(false)
    }

    /**
     * Send a message to all subscribers of a channel.
     *
     * @param channel to send a message
     * @param message object to send
     * @param meta additional metadata to send
     * @param store flag to keep the message in history
     */
    fun send(channel: String, message: MessageData, meta: Any? = null, store: Boolean = true) {

        // Just override status
        val newMessage = message.copy(isSent = false, exception = null)

        GlobalScope.launch(Dispatchers.IO) {
            // Add message to repository
            messageRepository.add(
                channelId = channel,
                message = newMessage,
            )

            Timber.d("Sending message: $newMessage")

            // Publish a message
            PubNubFramework.instance
                .publish(
                    channel = channel,
                    message = newMessage,
                    shouldStore = store,
                    meta = meta,
                )
                .single(
                    onComplete = { result ->
                        Timber.i("Message sent, result: $result")

                        // Set message status in repository
                        GlobalScope.launch(Dispatchers.IO) {
                            Timber.i("Message sent, set sent status")
                            messageRepository.setSent(
                                newMessage.id,
                                result.timetoken
                            )
                        }
                    },
                    onError = { exception ->
                        Timber.i("Message sending error, result: $exception")

                        // Set message status in repository
                        GlobalScope.launch(Dispatchers.IO) {
                            messageRepository.setSendingError(
                                newMessage.id,
                                exception.message
                            )
                        }
                    },
                )
        }
    }

    /**
     * Returns all messages as a [Flow]
     *
     * @return [Flow] of messages list sorted by timestamp
     */
    fun getAll(): Flow<List<T>> =
        messageRepository.getAll()
            .mapNotNull { list ->
                list.sortedBy { it.timestamp }.map { messageMapper.map(it) }
            }

    /**
     * Returns all messages from selected channel as a [Flow]
     *
     * @param channelId of messages
     *
     * @return [Flow] of messages list sorted by timestamp
     */
    fun getAll(channelId: String): Flow<List<T>> =
        messageRepository.getAll(channelId)
            .mapNotNull { list ->
                list.sortedBy { it.timestamp }.map { messageMapper.map(it) }
            }

    /**
     * Returns list of last N messages from channel
     *
     * @param channelId of messages
     * @param count of messages to return (default is 10)
     *
     * @return list of last N messages sorted by timestamp
     */
    fun getLast(
        channelId: String,
        count: Long = 10L,
    ): Flow<List<T>> =
        messageRepository.getLastByChannel(channelId, count)
            .mapNotNull { list ->
                list.sortedBy { it.timestamp }
                    .map { messageMapper.map(it) }
            }

    fun getPage(
        channelId: String,
    ): Flow<PagingData<T>> =
        Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = true),
            remoteMediator = MessageRemoteMediator(channelId, this, messageRepository),
            pagingSourceFactory = { messageRepository.getPage(channelId) },
        ).flow
            .map { data ->
                data.map { messageMapper.map(it) }
            }

    /**
     * Get historical messages between passed start - end dates and store it in database
     *
     * @param channelId of messages
     * @param start of time window, newest date (in microseconds)
     * @param end of time window, last known message timestamp + 1 (in microseconds)
     * @param count of messages, default and maximum is 100
     */
    suspend fun getHistory(
        channelId: String,
        start: Long?,
        end: Long?,
        count: Int,
    ): Boolean {
        val result = PubNubFramework.instance
            .history(
                channel = channelId,
                reverse = true,
                includeTimetoken = true,
                includeMeta = true,
                count = count,
                start = start,
                end = end,
            )
            .single()

        // Store messages
        result.messages.sortedByDescending { it.timetoken }.forEach {
            try {
                //todo just ignore files?
                val message = historyMapper.map(it)
//                Timber.e("New incoming message: $message")
                insertOrUpdate(channelId, message)
            } catch (e: Exception) {
                Timber.e(e, "Cannot map message [${it.timetoken}]")
                Timber.w("Message: ${it.entry}")
            }
        }

        return result.messages.size < count
    }

    fun getLastTimestamp(channelId: String) =
        runBlocking { messageRepository.getLastTimestamp(channelId) }

    // region Inner binding
    private fun listenForMessages() {
        GlobalScope.launch(Dispatchers.IO) {
            messageJob = PubNubFramework.instance.event.message
                .onEach { it.processMessage() }
                .launchIn(this)
        }
    }

    private fun stopListenForMessages() {
        messageJob.cancel()
    }
    // endregion

    /**
     * Processing [PNMessageResult] and silently catch exceptions
     *
     * @see [handleIncomingMessage]
     */
    private fun PNMessageResult.processMessage() {

        Timber.d("Message payload: $message")
        Timber.d("Message channel: $channel")
        Timber.d("Message publisher: $publisher")
        Timber.d("Message timetoken: $timetoken")
        Timber.d("User metadata: $userMetadata")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (!channelService.hasChannel(channel)) {
                    channelService.synchronizeChannel(channel)
                }

                handleIncomingMessage(this@processMessage)
            } catch (e: Exception) {
                Timber.e(e, "Cannot map message")
            }
        }
    }

    /**
     * Validates an incoming message, decrypt it and store in database
     */
    private fun handleIncomingMessage(result: PNMessageResult) {
        with(result) {
            if (publisher == null) {
                Timber.w("User cannot be null")
                return
            }
            if (timetoken == null) {
                Timber.w("Timestamp cannot be null")
                return
            }

            val messageData: MessageData = toMessageData()
            insertOrUpdate(channel, messageData)
        }
    }

    /**
     * Store new or update existing message
     */
    private fun insertOrUpdate(channelId: String, message: MessageData) {
        GlobalScope.launch(Dispatchers.IO) {
            runBlocking {
                if (messageRepository.has(message.id)) messageRepository.update(channelId, message)
                else messageRepository.add(channelId, message)
            }
        }
    }

    fun PNMessageResult.toMessageData(): MessageData =
        message.toJson().fromJson<MessageData>().copy(
            timestamp = timetoken!!,
            userId = publisher!!,

            // set status
            isSent = true,
            exception = null,
        )
}
