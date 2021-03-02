package com.pubnub.demo.telemedicine.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.pubnub.demo.telemedicine.util.CenterInside
import dev.chrisbanes.accompanist.coil.CoilImage

val HEADER_HEIGHT = 234.dp

@Composable
fun UserInfoDetails(
    name: String,
    description: String,
    imageUrl: String,
    modifier: Modifier = Modifier,
) {

    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
            CoilImage(
                request = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = null,
                contentScale = CenterInside,
                modifier = Modifier.size(104.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = description, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
@Preview
private fun UserDetailsPreview() {
    UserInfoDetails(
        name = "Saleha Ahmad",
        description = "47 yo · Pyrexia of unknown origin",
        imageUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/blob/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png",
    )
}