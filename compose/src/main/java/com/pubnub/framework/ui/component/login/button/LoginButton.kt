package com.pubnub.framework.ui.component.login.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pubnub.framework.ui.R
import com.pubnub.framework.ui.component.base.StyledButton
import com.pubnub.framework.ui.theme.PubNubTheme


@Composable
fun LoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StyledButton(
        onClick = onClick,
        text = stringResource(id = R.string.login_button),
        style = PubNubTheme.buttons.filled,
        modifier = modifier.fillMaxWidth()
    )
}