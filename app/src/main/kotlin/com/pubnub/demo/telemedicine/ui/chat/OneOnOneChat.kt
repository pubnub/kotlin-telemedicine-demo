package com.pubnub.demo.telemedicine.ui.chat

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.data.message.ReceiptData
import com.pubnub.demo.telemedicine.data.message.ReceiptTypeDef
import com.pubnub.demo.telemedicine.ui.conversation.Message
import com.pubnub.demo.telemedicine.ui.conversation.messageFormatter
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.seconds
import kotlinx.coroutines.flow.Flow
import org.joda.time.Days
import org.joda.time.LocalDate

@Composable
fun OneOnOneChat(
    messages: Flow<PagingData<Message>>,
    lastRead: Long,
    onMessageRead: (Timetoken) -> Unit,
    currentUserId: String,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
        val lazyMessages: LazyPagingItems<Message> = messages.collectAsLazyPagingItems()

        Box(modifier = modifier) {
            val lazyListState =
                rememberLazyListState(initialFirstVisibleItemIndex = lazyMessages.itemCount)

            LazyColumn(state = lazyListState, reverseLayout = true) {
                itemsIndexed(lazyMessages) { index, message ->
                    if (message == null) return@itemsIndexed

                    val prevMessage = lazyMessages.getOrNull(index - 1)
                    val nextMessage = lazyMessages.getOrNull(index + 1)

                    val prevAuthor = prevMessage?.authorId
                    val nextAuthor = nextMessage?.authorId

//                val prevDay = prevMessage?.timestamp?.seconds?.run { LocalDate(this) }
                    val currentDay = LocalDate(message.timestamp.seconds)
                    val nextDay = nextMessage?.timestamp?.seconds?.run { LocalDate(this) }

                    val isOwn = message.authorId == currentUserId
                    val isFirstMessageByAuthor = nextAuthor != message.authorId
                    val isLastMessageByAuthor = prevAuthor != message.authorId

                    // REVERSED LAYOUT: don't draw a header
                    val isDayChanged =
                        nextDay == null || Days.daysBetween(nextDay, currentDay).days != 0

                    val isDelivered = message.isDelivered
                    val isRead = message.timestamp <= lastRead
                    val status =
                        if (isRead) ReceiptData.Type.READ else if (isDelivered) ReceiptData.Type.DELIVERED else null

                    // Set read action
                    if (index == 0 && !isOwn && !isRead) {
                        onMessageRead(message.timestamp)
                    }
                    // First space between input and message
                    if (index == 0) Spacer(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd,
                    ) {
                        val styledMessage = messageFormatter(text = message.content)
                        OneOnOneChatMessage(
                            message = styledMessage,
                            imageUrl = message.image,
                            date = message.date,
                            isOwn = isOwn,
                            status = status,
                        )
                    }

                    // Day dividers
                    if (isDayChanged) {
                        val label = formatDate(message.timestamp)
                        DayDivider(label, Modifier.padding(12.dp))
                    } else {

                        if (isFirstMessageByAuthor) Spacer(modifier = Modifier.height(10.dp))
                        else Spacer(modifier = Modifier.height(5.dp))
                    }

                }
            }
        }
    }

private val OwnChatBubbleShape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 8.dp)
private val ChatBubbleShape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 0.dp)

@Composable
private fun OneOnOneChatMessage(
    message: AnnotatedString?,
    imageUrl: String?,
    date: String,
    isOwn: Boolean,
    @ReceiptTypeDef status: String? = null,
    modifier: Modifier = Modifier,
) {
    val paddingModifier = if(isOwn) Modifier.padding(56.dp, 0.dp, 0.dp, 0.dp) else Modifier.padding(0.dp, 0.dp, 56.dp, 0.dp)
    Row(
        modifier = modifier.fillMaxWidth().then(paddingModifier),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start,
    ) {
        val backgroundBubbleColor = if (isOwn) Color(0x8aa2e1db)
        else Color.White

        val chatBubble = if (isOwn) OwnChatBubbleShape else ChatBubbleShape

        Surface(color = backgroundBubbleColor, shape = chatBubble) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    if (message != null) ChatText(message)
                    if (imageUrl != null) ChatImage(imageUrl)
                    Row {
                        Date(text = date)
                        if (isOwn) {
                            Spacer(modifier = Modifier.width(4.dp))
                            drawReceipt(status = status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun drawReceipt(
    @ReceiptTypeDef status: String?,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF34B7F2),
) {
    Box(modifier = modifier.width(17.dp)) {
        if (status != null) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_receipt_delivered),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp, 11.dp),
            )
            if (status == ReceiptData.Type.READ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_receipt_read),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(11.dp, 11.dp)
                        .offset(x = 8.dp),
                )
            }
        }
    }
}

@Composable
@Preview
private fun OneOnOneChatMessageOnline() {
    OneOnOneChatMessage(
        message = messageFormatter(text = "Did you take x rays, too?"),
        imageUrl = null,
        date = "12:03pm",
        isOwn = false,
    )
}

@Composable
@Preview
private fun OneOnOneChatMessageOwn() {
    OneOnOneChatMessage(
        message = messageFormatter(text = "Yes, here is it"),
        imageUrl = null,
        date = "12:25pm",
        isOwn = true,
    )
}

@Composable
@Preview
private fun OneOnOneChatMessageDelivered() {
    OneOnOneChatMessage(
        message = messageFormatter(text = "Delivered message"),
        imageUrl = null,
        date = "12:25pm",
        isOwn = true,
        status = ReceiptData.Type.DELIVERED,
    )
}

@Composable
@Preview
private fun OneOnOneChatMessageRead() {
    OneOnOneChatMessage(
        message = messageFormatter(text = "Read message"),
        imageUrl = null,
        date = "12:25pm",
        isOwn = true,
        status = ReceiptData.Type.READ,
    )
}
