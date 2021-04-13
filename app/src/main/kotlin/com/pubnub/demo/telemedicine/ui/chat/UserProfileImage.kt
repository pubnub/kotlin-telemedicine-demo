package com.pubnub.demo.telemedicine.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.pubnub.demo.telemedicine.util.CenterInside
import com.pubnub.framework.ui.theme.green
import com.pubnub.framework.ui.theme.red
import com.google.accompanist.coil.CoilImage

@Composable
fun UserProfileImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    isOnline: Boolean,
    indicatorSize: Dp = 16.dp,
) {
    Box(modifier = modifier) {
        CoilImage(
            request = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = null,
            contentScale = CenterInside,
            modifier = Modifier.fillMaxSize(),
        )
        if (isOnline)
            OnlineIndicator(
                isOnline = isOnline,
                size = indicatorSize,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(-4.dp),
            )
    }
}

@Composable
fun OnlineIndicator(isOnline: Boolean, size: Dp = 16.dp, modifier: Modifier = Modifier) {
    val color: Color = if (isOnline) green else red
    val (borderStroke, borderColor) = (1.dp to Color.White)
    Box(modifier
        .size(size)
        .background(color, CircleShape)
        .border(borderStroke, borderColor, CircleShape))
}