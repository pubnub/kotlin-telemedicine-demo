package com.pubnub.demo.telemedicine.ui.conversation

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pubnub.demo.telemedicine.data.channel.ChannelType
import com.pubnub.demo.telemedicine.data.channel.ChannelTypeDef
import com.pubnub.demo.telemedicine.ui.Background
import com.pubnub.demo.telemedicine.ui.casestudy.ConversationViewModel
import com.pubnub.demo.telemedicine.ui.chat.ChatHeader
import com.pubnub.demo.telemedicine.ui.chat.ChatView
import com.pubnub.demo.telemedicine.ui.common.AppBar
import com.pubnub.demo.telemedicine.ui.common.FeatureNotImplemented
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber

@ExperimentalMaterialApi
@OptIn(ExperimentalCoroutinesApi::class)
@ObsoleteCoroutinesApi
@FlowPreview
@Composable
fun ChatActivityView(
    channelId: String,
    @ChannelTypeDef channelType: String,
    userId: UserId = PubNubFramework.userId,
) {

    Timber.e("Chat with id: $channelId")
    val conversationViewModel: ConversationViewModel = viewModel(key = "channelVM_$channelId")
    val messageViewModel: MessageViewModel = viewModel(key = "messageVM_$channelId")

    LaunchedEffect(channelId) {
        messageViewModel.synchronize(channelId)
    }

    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    val channel = conversationViewModel.getChat(channelId)

    val uiState: ConversationUiState =
        conversationViewModel.getState(channel.id, channel.title, channel.description)

    // region Actions
    val sendMessageAction: (String) -> Unit =
        { message ->
            conversationViewModel.sendMessage(channelId, message)
        }
    val sendFileAction: (Uri) -> Unit =
        { fileUri ->
            conversationViewModel.sendFile(channelId, fileUri)
        }
    val typingAction: (Boolean) -> Unit =
        { isTyping -> messageViewModel.setTyping(channel.id, isTyping) }
    val readAction: (Timetoken) -> Unit =
        { timetoken -> messageViewModel.setRead(channel.id, timetoken) }
    val backAction = { activity.finish() }
    val callAction = if (channelType == ChannelType.DOCTOR) {
        { FeatureNotImplemented.toast(context) }
    } else null

    val navigateToProfile: (UserId) -> Unit = { userId ->
        conversationViewModel.navigateToProfile(userId)
    }
    val navigateToPatientInfo: () -> Unit = {
        val caseId: ChannelId = conversationViewModel.getLastCase(channel.user.id)!!.id
        conversationViewModel.navigateToPatientInfo(caseId)
    }

    Timber.e("Channel: $channel")
    val isOnline = channel.isOnline // SDK-307 comment - always show online indicator
    // endregion

    Background {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AppBar {
                    ChatHeader(
                        active = isOnline,
                        title = uiState.channelName,
                        description = uiState.channelDescription,
                        imageUrl = channel.user.imageUrl,
                        backAction = backAction,
                        profileAction = {
                            if (channelType == ChannelType.DOCTOR) navigateToProfile(channel.user.id)
                            else navigateToPatientInfo()
                        },
                        callAction = callAction,
                    )
                }
            },
        ) {

            ChatView(
                isGroupChat = false,
                uiState = uiState,
                navigateToProfile = navigateToProfile,
                onMessageSent = sendMessageAction,
                onFileSent = sendFileAction,
                currentUserId = userId,
                onTyping = typingAction,
                isTyping = messageViewModel.isTyping(uiState.channelId),
                onRead = readAction,
                modifier = Modifier.background(Color(0xFFf8f8f8)),
            )
        }
    }
}
