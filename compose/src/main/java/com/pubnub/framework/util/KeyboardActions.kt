package com.pubnub.framework.util

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction

fun KeyboardAllActions(action: (ImeAction) -> Unit): KeyboardActions = KeyboardActions(
    onDone = { action(ImeAction.Done) },
    onGo = { action(ImeAction.Go) },
    onNext = { action(ImeAction.Next) },
    onPrevious = { action(ImeAction.Previous) },
    onSearch = { action(ImeAction.Search) },
    onSend = { action(ImeAction.Send) }
)
