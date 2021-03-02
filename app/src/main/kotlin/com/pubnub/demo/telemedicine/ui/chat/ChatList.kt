package com.pubnub.demo.telemedicine.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pubnub.demo.telemedicine.ui.conversation.Chat
import com.pubnub.demo.telemedicine.ui.conversation.messageFormatter
import com.pubnub.demo.telemedicine.ui.dashboard.DateLabel
import com.pubnub.demo.telemedicine.ui.dashboard.Description
import com.pubnub.demo.telemedicine.ui.dashboard.Label
import com.pubnub.demo.telemedicine.ui.dashboard.LabelSmall
import com.pubnub.demo.telemedicine.ui.dashboard.NotificationBadge
import com.pubnub.framework.data.ChannelId

private val DividerModifier = Modifier.padding(95.dp, 0.dp, 20.dp, 0.dp)
private val CardModifier = Modifier.padding(20.dp)

@Composable
fun ChatList(chats: List<Chat>, onClick: (ChannelId) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(chats) { index, chat ->
            ChatCard(
                active = chat.isOnline,
                imageUrl = chat.user.imageUrl,
                title = chat.title,
                description = chat.description,
                content = messageFormatter(text = chat.lastMessage?.content ?: ""),
                notifications = chat.notifications,
                changeTime = chat.lastMessage?.dateString ?: "",
                modifier = Modifier
                    .clickable(onClick = { onClick(chat.id) })
                    .then(CardModifier)
            )
            if (index < chats.size - 1)
                Divider(modifier = DividerModifier)
        }
    }
}

@Composable
fun ChatCard(
    active: Boolean,
    title: String,
    description: String,
    content: AnnotatedString,
    imageUrl: String? = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png",
    changeTime: String = "09:47 AM",
    notifications: Long = 0L,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        UserProfileImage(
            modifier = Modifier.size(55.dp),
            imageUrl = imageUrl,
            isOnline = active,
            indicatorSize = 16.dp
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Row {
                Label(text = title, modifier = Modifier.weight(1f))
                DateLabel(text = changeTime)
            }
            Spacer(Modifier.height(4.dp))
            LabelSmall(text = description)
            Spacer(Modifier.height(2.dp))
            if (content.text.isNotEmpty())
                Row(verticalAlignment = Alignment.Bottom) {
                    Description(text = content, maxLines = 1, modifier = Modifier.weight(1f))
                    NotificationBadge(notifications)
                }
        }
    }
}

@Composable
@Preview
fun ChatCardOnlinePreview() {
    ChatCard(
        active = true,
        title = "Saleha Ahmad",
        description = "47 yo • Pyrexia of unknown origin",
        content = messageFormatter("Thank you"),
        changeTime = "2:53pm",
        notifications = 1
    )
}

@Composable
fun Chats(
    chats: List<Chat>,
    onClick: (ChannelId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ChatList(chats, onClick)
    }
}

