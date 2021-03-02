package com.pubnub.framework.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale

/**
 * Default styles for buttons
 */
data class Buttons(
    /**
     * Default filled button
     */
    val filled: ButtonsStyle = ButtonsStyle(),
    /**
     * Outlined button
     */
    val outlined: ButtonsStyle = ButtonsStyle(),
    /**
     * Text button
     */
    val text: ButtonsStyle = ButtonsStyle(),
    /**
     * Underlined Text button
     */
    val textLink: ButtonsStyle = ButtonsStyle()
) {
    companion object {
        @Composable
        fun getDefault(colors: Colors, typography: Typography, shapes: Shapes): Buttons = Buttons(
            filled = ButtonsStyle(
                textStyle = typography.button.copy(color = colors.onSecondary),
                textDecoration = TextDecoration.None,
                textMapper = { it.toUpperCase(Locale.getDefault()) },

                shape = RoundedCornerShape(4.dp),
                backgroundColor = colors.secondary,
                border = null,

                disabledContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                    .compositeOver(MaterialTheme.colors.surface),
            ),

            outlined = ButtonsStyle(
                textStyle = typography.button,
                textDecoration = TextDecoration.None,
                textMapper = { it.toUpperCase(Locale.getDefault()) },

                shape = RoundedCornerShape(50),
                backgroundColor = Color.Unspecified,
                contentColor = colors.primary,
                border = BorderStroke(2.dp, colors.primary),
                elevation = 0.dp,

                disabledContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                    .compositeOver(MaterialTheme.colors.surface),
            ),
            text = ButtonsStyle(
                textStyle = typography.button.copy(color = colors.secondary),
                textDecoration = TextDecoration.None,
                textMapper = { it.toUpperCase(Locale.getDefault()) },

                shape = shapes.small,
                backgroundColor = Color.Transparent,
                contentColor = colors.secondary,
                border = null,
                elevation = 0.dp,

                disabledContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                    .compositeOver(MaterialTheme.colors.surface),

                padding = ButtonDefaults.ContentPadding
            ),
            textLink = ButtonsStyle(
                textStyle = typography.button.copy(color = colors.secondary),
                textDecoration = TextDecoration.Underline,

                shape = shapes.small,
                backgroundColor = Color.Transparent,
                contentColor = colors.secondary,
                border = null,
                elevation = 0.dp,

                disabledContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                    .compositeOver(MaterialTheme.colors.surface),

                padding = ButtonDefaults.ContentPadding
            )
        )
    }
}

data class ButtonsStyle(
    val textStyle: TextStyle = TextStyle(),
    val textDecoration: TextDecoration = TextDecoration.None,
    val textMapper: (String) -> String = { it },

    val shape: Shape = RoundedCornerShape(4.dp),
    val backgroundColor: Color = Color.White,
    val contentColor: Color = Color.Black,
    val border: BorderStroke? = null,

    val disabledContentColor: Color = Color.Black,
    val disabledBackgroundColor: Color = Color.Black,
    val padding: PaddingValues = PaddingValues(16.dp, 12.dp, 16.dp, 12.dp),
    val elevation: Dp = 0.dp,
    val pressedElevation: Dp = 0.dp,
    val disabledElevation: Dp = 0.dp,
)

/**
 * Ambient used to specify the default buttons for the surfaces.
 */
internal val ButtonsAmbient = staticCompositionLocalOf { Buttons() }
