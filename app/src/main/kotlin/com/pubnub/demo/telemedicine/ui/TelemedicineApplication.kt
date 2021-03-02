package com.pubnub.demo.telemedicine.ui

import android.app.Application
import com.pubnub.demo.telemedicine.AppDatabase
import com.pubnub.demo.telemedicine.BuildConfig
import com.pubnub.demo.telemedicine.network.service.ChannelService
import com.pubnub.demo.telemedicine.network.service.MessageService
import com.pubnub.demo.telemedicine.network.service.ReceiptService
import com.pubnub.demo.telemedicine.ui.conversation.Message
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.OccupancyService
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltAndroidApp
class TelemedicineApplication : Application() {

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var receiptService: Lazy<ReceiptService>

    @Inject
    lateinit var messageService: Lazy<MessageService<Message>>

    @Inject
    lateinit var occupancyService: Lazy<OccupancyService>

    @Inject
    lateinit var channelService: Lazy<ChannelService>

    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        Timber.i("Database initialized: $database")
    }

    fun onLogin(userId: UserId) {

        // set user id and navigate to MainActivity
        PubNubFramework.initialize(
            BuildConfig.PUB_KEY,
            BuildConfig.SUB_KEY,
            userId,
            BuildConfig.CIPHER_KEY,
        )

        listenForMessages()
    }

    fun onLogout() {
        stopListenForMessages()
        PubNubFramework.instance.destroy()
    }

    /**
     * Listen for incoming messages
     * Subscribe to all the cases
     */
    private fun listenForMessages() {
        Timber.i("-> Listen for messages")
        GlobalScope.launch(Dispatchers.IO) {
            receiptService.get().bind()
            messageService.get().bind()
            channelService.get().getUserChannelGroupIds().onEach { channelGroupList ->
                val channelGroups = channelGroupList.toTypedArray()
                Timber.e("New channelGroups: ${channelGroups.joinToString(", ")}")
                occupancyService.get().setChannelGroups(*channelGroups)
            }.launchIn(this)
        }
    }

    /**
     * Listen for incoming messages
     * Subscribe to all the cases
     */
    private fun stopListenForMessages() {
        Timber.i("<- Stop listening for messages")
        GlobalScope.launch {
            receiptService.get().unbind()
            messageService.get().unbind()
            occupancyService.get().removeAll()
        }
    }
}
