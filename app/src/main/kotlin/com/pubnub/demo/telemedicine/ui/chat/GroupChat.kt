package com.pubnub.demo.telemedicine.ui.chat

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.ui.conversation.Message
import com.pubnub.demo.telemedicine.ui.conversation.SymbolAnnotationType
import com.pubnub.demo.telemedicine.ui.conversation.messageFormatter
import com.pubnub.demo.telemedicine.util.coil.PubNubEncryptedImageDecoder
import com.pubnub.demo.telemedicine.util.extension.isToday
import com.pubnub.demo.telemedicine.util.extension.isYesterday
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.seconds
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.LocalDate
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
@FlowPreview
@Composable
fun GroupChat(
    messages: Flow<PagingData<Message>>,
    lastRead: Long,
    occupants: List<UserId>,
    navigateToProfile: (String) -> Unit,
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

                val isFirstMessageByAuthor = nextAuthor != message.authorId
                val isLastMessageByAuthor = prevAuthor != message.authorId

                // REVERSED LAYOUT: don't draw a header
                val isDayChanged =
                    nextDay == null || Days.daysBetween(nextDay, currentDay).days != 0

//                val isRead = message.timestamp <= lastRead

                // First space between input and message
                if (index == 0) Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd,
                ) {
                    val styledMessage = messageFormatter(text = message.content)
                    GroupChatMessage(
                        userId = message.authorId,
                        profileUrl = message.avatarUrl,
                        title = message.author,
                        message = styledMessage,
                        imageUrl = message.image,
                        date = message.date,
                        showProfileImage = isLastMessageByAuthor,
                        isOnline = occupants.contains(message.authorId),
                        isOwn = message.authorId == currentUserId,
                        navigateToProfile = navigateToProfile,
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

@Composable
internal fun formatDate(timestamp: Timetoken): String {
    val resources = LocalContext.current.resources
    val date = DateTime(timestamp.seconds)
    return when {
        date.isToday() -> resources.getString(R.string.case_date_today)
        date.isYesterday() -> resources.getString(R.string.case_date_format_yesterday)
        else -> date.toString(resources.getString(R.string.case_date_format_before_yesterday))
    }.toUpperCase()
}

internal fun <T : Any> LazyPagingItems<T>.getOrNull(i: Int): T? {
    return if (i !in 0 until itemCount) null
    else this[i]
}

@Composable
fun DayDivider(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        Card(backgroundColor = Color(0xFFdbeefc)) {
            Text(
                text = text,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.54f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(8.dp, 2.dp)
            )
        }
    }
}

private val ChatBubbleShape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 0.dp)

@Composable
fun GroupChatMessage(
    userId: UserId,
    profileUrl: String?,
    title: String,
    message: AnnotatedString?,
    imageUrl: String?,
    date: String,
    showProfileImage: Boolean,
    isOnline: Boolean,
    isOwn: Boolean,
    modifier: Modifier = Modifier,
    navigateToProfile: (UserId) -> Unit,
) {

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Box(
            modifier = Modifier.size(32.dp),
        ) {
            if (showProfileImage) {
                UserProfileImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = { navigateToProfile(userId) }),
                    imageUrl = profileUrl,
                    isOnline = !isOwn && isOnline,
                    indicatorSize = 8.dp,
                )
            }
        }
        Spacer(modifier = Modifier.width(5.dp))

        val backgroundBubbleColor = if (isOwn) Color(0x8aa2e1db)
        else Color.White

        Surface(color = backgroundBubbleColor, shape = ChatBubbleShape) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Column(Modifier.padding(8.dp)) {
                    Title(text = title, isOwn = isOwn)
                    Spacer(modifier = Modifier.width(5.dp))
                    if (message != null) ChatText(message, navigateToProfile)
                    if (imageUrl != null) ChatImage(imageUrl)
                    Date(text = date)
                }
            }
        }
    }
}

@Composable
private fun Title(text: String, isOwn: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = if (isOwn) Color(0xFFc78502) else Color(0xDE02a7fa),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
fun Date(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.54f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
fun ChatText(
    message: AnnotatedString,
    navigateToProfile: (UserId) -> Unit = { Timber.w("Navigate to profile $it not implemented") },
) {
    val uriHandler = LocalUriHandler.current
    ClickableText(
        text = message,
        style = TextStyle(
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        ),
        onClick = {
            message
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    Timber.e("Annotation $annotation")
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> navigateToProfile(annotation.item)
                        else -> Unit
                    }
                }
        }
    )
}

@Composable
fun ChatImage(
    imageUrl: String,
) {
    // TODO: 1/27/21 extract ImageRequest Builder to PubNub utils
    CoilImage(
        request = ImageRequest.Builder(LocalContext.current)
            .decoder(PubNubEncryptedImageDecoder(LocalContext.current,
                PubNubFramework.instance))
            .crossfade(true)
            .data(imageUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = null,
        fadeIn = true,
        modifier = Modifier
            .width(280.dp)
            .padding(8.dp),
        //.clickable(onClick = onImageClick),
    )
}

@Composable
@Preview
private fun DayDividerToday() {
    DayDivider(text = "TODAY")
}

@Composable
@Preview
private fun GroupChatMessageOnline() {
    GroupChatMessage(
        userId = "userId",
        profileUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/young_handsome_african_man_doctor_gray-1e8bfcee7c1b1e94d0f3e4be29ceccca-ecdf18.png",
        title = "Dr Badri",
        message = messageFormatter(text = "Did you take x rays, too?"),
        imageUrl = null,
        date = "12:03pm",
        showProfileImage = true,
        isOwn = false,
        isOnline = true,
        navigateToProfile = {},
    )
}

@Composable
@Preview
private fun GroupChatMessageOwn() {
    GroupChatMessage(
        userId = "userId",
        profileUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/portrait_of_doctor-7af1f85a067d4dbae743e8527a20b93f-8fb27d.png",
        title = "Dr Zaidi",
        message = messageFormatter(text = "Yes, here is it"),
        imageUrl = null,
        date = "12:25pm",
        showProfileImage = true,
        isOwn = true,
        isOnline = false,
        navigateToProfile = {},
    )
}

