package com.pubnub.demo.telemedicine.ui.theme

import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pubnub.framework.ui.theme.PubNubTheme
import com.pubnub.framework.ui.theme.cyan
import com.pubnub.framework.ui.theme.cyanDark
import com.pubnub.framework.ui.theme.purple
import com.pubnub.framework.ui.theme.purpleDark

private val LightColorAppPalette = lightColors(
    primary = cyan,
    primaryVariant = cyanDark,
    secondary = purple,
    secondaryVariant = purpleDark,
    onSecondary = Color.White,
)

@Composable
fun AppTheme(content: @Composable() () -> Unit) {
    val colors = LightColorAppPalette

    PubNubTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}