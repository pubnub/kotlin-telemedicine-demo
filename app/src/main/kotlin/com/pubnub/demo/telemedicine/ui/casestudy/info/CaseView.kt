package com.pubnub.demo.telemedicine.ui.casestudy.info

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pubnub.demo.telemedicine.ui.Background
import com.pubnub.demo.telemedicine.ui.casestudy.ConversationViewModel
import com.pubnub.demo.telemedicine.ui.chat.CaseHeader
import com.pubnub.demo.telemedicine.ui.chat.ChatView
import com.pubnub.demo.telemedicine.ui.common.AppBar
import com.pubnub.demo.telemedicine.ui.common.FeatureNotImplemented
import com.pubnub.demo.telemedicine.ui.conversation.ConversationUiState
import com.pubnub.demo.telemedicine.ui.conversation.MessageViewModel
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.FlowPreview
import timber.log.Timber


@OptIn(ExperimentalMaterialApi::class, FlowPreview::class)
@Composable
fun CaseView(caseId: String, userId: UserId) {

    Timber.e("Case with id: $caseId")
    val conversationViewModel: ConversationViewModel = viewModel(key = "channelVM_$caseId")
    val messageViewModel: MessageViewModel = viewModel(key = "messageVM_$caseId")

    LaunchedEffect(caseId) {
        messageViewModel.synchronize(caseId)
    }

    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    val case = conversationViewModel.getCase(caseId)!!

    val uiState: ConversationUiState =
        conversationViewModel.getState(caseId, case.title, case.description)

    // region Actions
    val sendMessageAction: (String) -> Unit =
        { message ->
            conversationViewModel.sendMessage(caseId, message)
        }
    val sendFileAction: (Uri) -> Unit =
        { fileUri ->
            conversationViewModel.sendFile(caseId, fileUri)
        }
    val backAction = { activity.finish() }
    val addAction = { FeatureNotImplemented.toast(context) }
    val typingAction: (Boolean) -> Unit =
        { isTyping -> messageViewModel.setTyping(case.id, isTyping) }
    val navigateToPatientInfo: () -> Unit = { conversationViewModel.navigateToPatientInfo(caseId) }
    // endregion

    val state = rememberScaffoldState()

    Background {

        Scaffold(
            scaffoldState = state,
            topBar = {
                AppBar {
                    CaseHeader(
                        title = case.title,
                        description = case.description,
                        backAction = backAction,
                        addAction = addAction,
                        modifier = Modifier.clickable(onClick = navigateToPatientInfo)
                    )
                }
            },
        ) {
            ChatView(
                isGroupChat = true,
                uiState = uiState,
                navigateToProfile = { Timber.e("Navigate to profile: $it") },
                onMessageSent = sendMessageAction,
                onFileSent = sendFileAction,
                currentUserId = userId,
                onTyping = typingAction,
                isTyping = messageViewModel.isTyping(uiState.channelId),
                modifier = Modifier.background(Color(0xFFf8f8f8)),
            )
        }
    }
}
