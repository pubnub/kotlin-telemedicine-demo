package com.pubnub.demo.telemedicine.ui.conversation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class ConversationUiState(
    val channelId: ChannelId,
    val channelName: String,
    val channelDescription: String,
    val messages: Flow<PagingData<Message>>,
    val lastReadTimestamp: State<Long>,
    val occupants: State<List<UserId>> = mutableStateOf(emptyList()),
)

@Immutable
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val author: String,
    val authorId: UserId,
    val content: String,
    val timestamp: Timetoken,
    val date: String = "",
    val image: String? = null,
    val avatarUrl: String? = null,
    val isDelivered: Boolean = false,
)