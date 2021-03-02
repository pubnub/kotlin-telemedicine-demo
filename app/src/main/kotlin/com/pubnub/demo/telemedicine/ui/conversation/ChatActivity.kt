package com.pubnub.demo.telemedicine.ui.conversation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import com.pubnub.demo.telemedicine.network.service.ChannelService
import com.pubnub.demo.telemedicine.ui.common.LocalBackDispatcher
import com.pubnub.framework.PubNubFramework
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ObsoleteCoroutinesApi
@OptIn(ExperimentalCoroutinesApi::class)
@FlowPreview
@AndroidEntryPoint
class ChatActivity : ComponentActivity() {

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val CHANNEL_TYPE = "channel_type"
    }

    @Inject
    lateinit var channelService: ChannelService

    lateinit var channelId: String

    lateinit var channelType: String

    val userId get() = PubNubFramework.userId

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelId = intent.getStringExtra(CHANNEL_ID)!!
        channelType = intent.getStringExtra(CHANNEL_TYPE)!!
        setContent {
            CompositionLocalProvider(LocalBackDispatcher provides onBackPressedDispatcher) {
                ChatActivityView(channelId, channelType)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            channelService.setReadAt(channelId)
        }
    }

    override fun onPause() {
        super.onPause()
        GlobalScope.launch(Dispatchers.IO) {
            channelService.setReadAt(channelId)
        }
    }
}
