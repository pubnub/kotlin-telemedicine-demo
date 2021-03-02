package com.pubnub.demo.telemedicine.ui.chat

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pubnub.demo.telemedicine.ui.conversation.ConversationUiState
import com.pubnub.framework.data.Typing
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import timber.log.Timber

@OptIn(
    ExperimentalCoroutinesApi::class,
    ObsoleteCoroutinesApi::class,
    ExperimentalMaterialApi::class,
    FlowPreview::class,
)
@Composable
fun ChatView(
    uiState: ConversationUiState,
    navigateToProfile: (String) -> Unit,
    currentUserId: String,
    modifier: Modifier = Modifier,
    isGroupChat: Boolean = false,
    onMessageSent: (String) -> Unit = { message -> },
    onFileSent: (Uri) -> Unit = { _ -> },
    onTyping: (Boolean) -> Unit = {},
    isTyping: List<Typing> = emptyList(),
    onRead: (Timetoken) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Column(modifier.fillMaxSize()) {
        val innerModifier = Modifier
            .weight(1f)
            .padding(10.dp, 0.dp)

        if (isGroupChat)
            GroupChat(
                messages = uiState.messages,
                lastRead = uiState.lastReadTimestamp.value,
                occupants = uiState.occupants.value,
                navigateToProfile = navigateToProfile,
                currentUserId = currentUserId,
                modifier = innerModifier,
                scrollState = scrollState,
            )
        else
            OneOnOneChat(
                messages = uiState.messages,
                lastRead = uiState.lastReadTimestamp.value,
                onMessageRead = onRead,
                currentUserId = currentUserId,
                modifier = innerModifier,
                scrollState = scrollState,
            )

        TypingIndicator(
            data = isTyping,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 8.dp)
        )

        ChatInput(
            onMessageSent = onMessageSent,
            onFileSent = onFileSent,
            onTyping = onTyping,
            scrollState = scrollState,
        )
    }

}

@Composable
fun TypingIndicator(
    data: List<Typing>?,
    modifier: Modifier = Modifier,
) {
    Timber.e("isTyping: $data")
    val lastData = data?.filter { it.isTyping }?.maxByOrNull { it.timestamp }
    val message = if (lastData?.isTyping != null) "${lastData.userId} is typing..." else return

    Text(
        text = message,
        modifier = modifier,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.54f),
        fontSize = 12.sp,
        fontStyle = FontStyle.Normal
    )
}