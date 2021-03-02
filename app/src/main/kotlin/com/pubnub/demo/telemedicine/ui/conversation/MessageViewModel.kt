package com.pubnub.demo.telemedicine.ui.conversation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pubnub.demo.telemedicine.network.service.ReceiptService
import com.pubnub.demo.telemedicine.repository.ChannelRepository
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.Typing
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.TypingService
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
@FlowPreview
class MessageViewModel @ViewModelInject constructor(
    private val typingService: TypingService,
    private val receiptService: ReceiptService,
    private val channelRepository: ChannelRepository,
) : ViewModel() {

    val userId: UserId get() = PubNubFramework.userId

    @Composable
    fun isTyping(channelId: ChannelId): List<Typing> {
        DisposableEffect(channelId) {
            typingService.bind(channelId)

            onDispose {
                typingService.unbind()
            }
        }
        val typing by typingService.typing(channelId)
            .collectAsState(initial = emptyList())

        Timber.e("Typing: $typing")
        return typing//.value
    }

    fun setTyping(caseId: String, typing: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            typingService.setTyping(
                userId = PubNubFramework.userId,
                channelId = caseId,
                isTyping = typing
            )
        }
    }

    fun setRead(channelId: ChannelId, messageTimestamp: Timetoken) {
        GlobalScope.launch(Dispatchers.IO) {
            if (!receiptService.isRead(channelId, messageTimestamp, userId))
                receiptService.setRead(channelId, messageTimestamp)

            if (channelRepository.get(channelId)?.metadata?.readAt ?: 0 < messageTimestamp)
                channelRepository.setReadAt(channelId, messageTimestamp)
        }
    }

    fun synchronize(channelId: ChannelId) {
        GlobalScope.launch(Dispatchers.IO) {
            receiptService.synchronize(channelId)
        }
    }
}