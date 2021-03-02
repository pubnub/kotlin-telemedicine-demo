package com.pubnub.framework.ui.component.base

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.pubnub.framework.ui.theme.ButtonsStyle
import com.pubnub.framework.ui.theme.PubNubTheme

/**
 * Styled button
 */
@Composable
fun StyledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ButtonsStyle = PubNubTheme.buttons.filled,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    text: String
) {
    return Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = ButtonDefaults.elevation(defaultElevation = style.elevation,
            pressedElevation = style.pressedElevation,
            disabledElevation = style.disabledElevation
        ),
        shape = style.shape,
        border = style.border,
        colors = ButtonDefaults.buttonColors(backgroundColor = style.backgroundColor,
            disabledBackgroundColor = style.disabledBackgroundColor,
            contentColor = contentColorFor(style.contentColor),
            disabledContentColor = contentColorFor(style.disabledContentColor)
        ),
        contentPadding = style.padding,
        content = {
            Text(
                style.textMapper(text),
                style = style.textStyle,
                textDecoration = style.textDecoration
            )
        }
    )
}

