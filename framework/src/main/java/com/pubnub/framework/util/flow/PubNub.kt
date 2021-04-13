package com.pubnub.framework.util.flow

import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.api.models.consumer.pubsub.objects.PNObjectEventResult
import com.pubnub.framework.util.data.PubNubEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach

fun PubNub.subscribeBy(
    onStatus: (PNStatus) -> Unit = {},
    onMessage: (PNMessageResult) -> Unit = {},
    onMessageAction: (PNMessageActionResult) -> Unit = {},
    onObjects: (PNObjectEventResult) -> Unit = {},
    onPresence: (PNPresenceEventResult) -> Unit = {},
    onSignal: (PNSignalResult) -> Unit = {}
){
    val callback: SubscribeCallback = object : SubscribeCallback(){
        override fun status(pubnub: PubNub, pnStatus: PNStatus) {
            onStatus.invoke(pnStatus)
        }

        override fun message(pubnub: PubNub, pnMessageResult: PNMessageResult) {
            onMessage.invoke(pnMessageResult)
        }

        override fun messageAction(pubnub: PubNub, pnMessageActionResult: PNMessageActionResult) {
            onMessageAction.invoke(pnMessageActionResult)
        }

        override fun objects(pubnub: PubNub, objectEvent: PNObjectEventResult) {
            onObjects.invoke(objectEvent)
        }

        override fun presence(pubnub: PubNub, pnPresenceEventResult: PNPresenceEventResult) {
            onPresence.invoke(pnPresenceEventResult)
        }

        override fun signal(pubnub: PubNub, pnSignalResult: PNSignalResult) {
            onSignal.invoke(pnSignalResult)
        }
    }
    this.addListener(callback)
}

val PubNub.event
    get(): Flow<PubNubEvent> =
        callbackFlow {
            val callback: SubscribeCallback = object : SubscribeCallback(){
                override fun status(pubnub: PubNub, pnStatus: PNStatus) {
                    sendBlocking(pnStatus)
                }

                override fun message(pubnub: PubNub, pnMessageResult: PNMessageResult) {
                    sendBlocking(pnMessageResult)
                }

                override fun messageAction(pubnub: PubNub, pnMessageActionResult: PNMessageActionResult) {
                    sendBlocking(pnMessageActionResult)
                }

                override fun objects(pubnub: PubNub, objectEvent: PNObjectEventResult) {
                    sendBlocking(objectEvent)
                }

                override fun presence(pubnub: PubNub, pnPresenceEventResult: PNPresenceEventResult) {
                    sendBlocking(pnPresenceEventResult)
                }

                override fun signal(pubnub: PubNub, pnSignalResult: PNSignalResult) {
                    sendBlocking(pnSignalResult)
                }
            }
            this@event.addListener(callback)

            awaitClose { this@event.removeListener(callback) }
        }

fun Flow<PubNubEvent>.onMessage(action: (PNMessageResult) -> Unit): Flow<PubNubEvent> =
    onEach { if(it is PNMessageResult) action.invoke(it) }

fun Flow<PubNubEvent>.onMessageAction(action: (PNMessageActionResult) -> Unit): Flow<PubNubEvent> =
    onEach { if(it is PNMessageActionResult) action.invoke(it) }

fun Flow<PubNubEvent>.onSignal(action: (PNSignalResult) -> Unit): Flow<PubNubEvent> =
    onEach { if(it is PNSignalResult) action.invoke(it) }

fun Flow<PubNubEvent>.onObject(action: (PNObjectEventResult) -> Unit): Flow<PubNubEvent> =
    onEach { if(it is PNObjectEventResult) action.invoke(it) }

fun Flow<PubNubEvent>.onPresence(action: (PNPresenceEventResult) -> Unit): Flow<PubNubEvent> =
    onEach { if(it is PNPresenceEventResult) action.invoke(it) }

@Suppress("UNCHECKED_CAST")
val Flow<PubNubEvent>.message: Flow<PNMessageResult>
    get() = filter { it is PNMessageResult } as Flow<PNMessageResult>

@Suppress("UNCHECKED_CAST")
val Flow<PubNubEvent>.messageAction: Flow<PNMessageActionResult>
    get() = filter { it is PNMessageActionResult } as Flow<PNMessageActionResult>

@Suppress("UNCHECKED_CAST")
val Flow<PubNubEvent>.signal: Flow<PNSignalResult>
    get() = filter { it is PNSignalResult } as Flow<PNSignalResult>

@Suppress("UNCHECKED_CAST")
val Flow<PubNubEvent>.`object`: Flow<PNObjectEventResult>
    get() = filter { it is PNObjectEventResult } as Flow<PNObjectEventResult>

@Suppress("UNCHECKED_CAST")
val Flow<PubNubEvent>.presence: Flow<PNPresenceEventResult>
    get() = filter { it is PNPresenceEventResult } as Flow<PNPresenceEventResult>