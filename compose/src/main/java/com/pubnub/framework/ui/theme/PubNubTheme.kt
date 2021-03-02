package com.pubnub.framework.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun PubNubTheme(
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    buttons: Buttons = Buttons.getDefault(colors, typography, shapes),
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
    ) {
        CompositionLocalProvider(ButtonsAmbient provides buttons) {
            content.invoke()
        }
    }
}

/**
 * Contains functions to access the current theme values provided at the call site's position in
 * the hierarchy.
 */
object PubNubTheme {
    /**
     * Retrieves the current [ButtonsStyle] at the call site's position in the hierarchy.
     */
    val buttons: Buttons
        @Composable
        get() = ButtonsAmbient.current
}
