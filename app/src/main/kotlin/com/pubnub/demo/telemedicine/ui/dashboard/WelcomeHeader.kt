package com.pubnub.demo.telemedicine.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.ui.common.HeaderSize

// region Header
@Composable
internal fun WelcomeHeader(title: String) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = HeaderSize.then(HeaderPadding),
    ) {
        Text(
            text = stringResource(id = R.string.welcome_message, title),
            style = MaterialTheme.typography.h4,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(name = "Default welcome header")
@Composable
private fun WelcomeHeaderPreview() {
    WelcomeHeader(title = "Dr House")
}

@Preview(name = "Long username will be ellipsized")
@Composable
private fun WelcomeHeaderLongPreview() {
    WelcomeHeader(title = "Dr House With So Long Name Should Be Ellipsized And Take Up To Two Lines")
}

private val HeaderPadding =
    Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 20.dp)
// endregion
