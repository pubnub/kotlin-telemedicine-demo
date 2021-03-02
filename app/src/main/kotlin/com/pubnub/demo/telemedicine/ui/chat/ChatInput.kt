package com.pubnub.demo.telemedicine.ui.chat

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.NoteAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.ui.common.FeatureNotImplemented
import com.pubnub.demo.telemedicine.ui.common.backHandler
import com.pubnub.framework.util.KeyboardAllActions
import com.pubnub.framework.util.registerForActivityResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber


enum class InputSelector {
    NONE,
    FILE
}

@ExperimentalMaterialApi
@Preview
@Composable
fun ChatInputPreview() {
    ChatInput(
        onMessageSent = {},
        onFileSent = {}
    )
}


@OptIn(ExperimentalComposeApi::class)
@ExperimentalMaterialApi
@Composable
fun ChatInput(
    onMessageSent: (String) -> Unit,
    onFileSent: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    onTyping: (isTyping: Boolean) -> Unit = {},
    scrollState: ScrollState? = null,
    selectorInit: InputSelector = InputSelector.NONE,
) {
    val scope = rememberCoroutineScope()
    var currentInputSelector by rememberSaveable { mutableStateOf(selectorInit) }
    val dismissKeyboard = { currentInputSelector = InputSelector.NONE }

    // Intercept back navigation if there's a InputSelector visible
    if (currentInputSelector != InputSelector.NONE) {
        backHandler(onBack= dismissKeyboard)
    }

    val currentFile: MutableState<Uri?> = remember { mutableStateOf(null) }
    val onFileSelected: (Uri?) -> Unit = { fileUri ->
        dismissKeyboard()
        if (fileUri != null) onFileSent(fileUri)
    }
    val onFileRemoved: () -> Unit = {
        currentFile.value = null
    }
    var textState by remember { mutableStateOf(TextFieldValue()) }

    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }


    val sendMessage = {
        currentFile.value?.let { onFileSent(it) }
        if (textState.text.isNotEmpty()) onMessageSent(textState.text)
        onTyping(false)

        // Reset file
        currentFile.value = null
        // Reset text field and close keyboard
        textState = TextFieldValue()
        // Move scroll to bottom
        scope.launch {
            scrollState?.animateScrollTo(0)
        }
        dismissKeyboard()
    }

    Column(modifier) {
        //Divider()
        SelectorExpanded(
            inputSelector = currentInputSelector,
            onFileSelected = onFileSelected,
        )
        Row(modifier
            .fillMaxWidth()
            .background(Color.White)) {
            ChatInputText(
                textFieldValue = textState,
                onTextChanged = {
                    if (it.text != textState.text)
                        onTyping(it.text.isNotBlank())
                    textState = it
                },
                // Only show the keyboard if there's no input selector and text field has focus
                keyboardShown = currentInputSelector == InputSelector.NONE && textFieldFocusState,
                // Close extended selector if text field receives focus
                onTextFieldFocused = { focused ->
                    if (focused) {
                        currentInputSelector = InputSelector.NONE
                        scope.launch {
                            scrollState?.animateScrollTo(0)
                        }
                    }
                    textFieldFocusState = focused
                },
                focusState = textFieldFocusState,
                onSelectorChange = {
                    currentInputSelector = if (currentInputSelector == InputSelector.NONE) it
                    else InputSelector.NONE
                },
                onImeAction = { action ->
                    if (action == ImeAction.Send) {
                        sendMessage()
                    }
                },
                onFileRemoved = onFileRemoved,
                modifier = Modifier
                    .weight(1f)
                    .height(ChatInputPreferredHeight)
            )

            ChatInputSelector(
                sendMessageEnabled = textState.text.isNotBlank(),
                onMessageSent = sendMessage,
            )
        }
    }
}

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

val ChatInputPreferredHeight = 50.dp

@Composable
private fun ChatInputText(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean,
    onSelectorChange: (InputSelector) -> Unit,
    onImeAction: (ImeAction) -> Unit,
    modifier: Modifier = Modifier,
    onFileRemoved: () -> Unit = {},
) {
    val a11ylabel = stringResource(id = R.string.textfield_desc)

    Row(
        modifier = modifier
            .semantics {
                contentDescription = a11ylabel
                keyboardShownProperty = keyboardShown
            },
        horizontalArrangement = Arrangement.End
    ) {
        //Surface {
        Row {

            ImageSelector(
                onFileRemoved = onFileRemoved,
                onSelector = { onSelectorChange(InputSelector.FILE) }
            )

            Box(
                modifier = Modifier
                    .height(ChatInputPreferredHeight)
                    .weight(1f)
                    .align(Alignment.Bottom)
            ) {
                var lastFocusState by remember { mutableStateOf(FocusState.Inactive) }
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { onTextChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                        .align(Alignment.CenterStart)
                        .onFocusChanged { state ->
                            if (lastFocusState != state) {
                                onTextFieldFocused(state == FocusState.Active)
                            }
                            lastFocusState = state
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardAllActions { action -> onImeAction(action) },
                    singleLine = true,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                )

                val disableContentColor =
                    MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                if (textFieldValue.text.isEmpty() && !focusState) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp),
                        text = stringResource(id = R.string.textfield_hint),
                        style = MaterialTheme.typography.body1.copy(color = disableContentColor)
                    )
                }

            }
        }
    }
}

@Composable
fun ImageSelector(
    modifier: Modifier = Modifier,
    uploading: Boolean = false,
    onFileRemoved: () -> Unit = {},
    onSelector: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {

            Icon(
                imageVector = Icons.Rounded.AddCircle,
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onSelector)
                    .padding(12.dp),
            )
    }
}

@ExperimentalMaterialApi
@Composable
private fun ChatInputSelector(
    sendMessageEnabled: Boolean,
    onMessageSent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .wrapContentHeight()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val border = if (!sendMessageEnabled) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
            )
        } else {
            null
        }
        val disabledContentColor =
            MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)

        val buttonColors =
            ButtonDefaults.buttonColors(
                disabledBackgroundColor = MaterialTheme.colors.surface,
                contentColor = Color(0xFF1fb7a9),
                disabledContentColor = disabledContentColor
            )

        // Send button
        IconButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(36.dp),
            enabled = sendMessageEnabled,
            onClick = onMessageSent,
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = null,
                tint = buttonColors.contentColor(sendMessageEnabled).value
            )
        }
    }
}

const val SELECT_IMAGE_REQUEST_CODE = 12345

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalComposeApi
@Composable
fun SelectorExpanded(
    inputSelector: InputSelector,
    onFileSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
) {

    // TEMPORARY ONLY
    val context = LocalContext.current
    if (inputSelector != InputSelector.FILE) return

    val selectFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        Timber.e("Select file: $uri")
        onFileSelected(uri)
    }
    val notImplementedAction = { FeatureNotImplemented.toast(context) }
    val galleryAction = {
            Timber.e("Select file")
            selectFile.launch("image/*")
    }

    val items = listOf(
        FileSelectorItem(
            title = stringResource(id = R.string.file_selector_gallery),
            icon = Icons.Rounded.Image,
            background = Color(0xFF7c86fb),
            action = galleryAction,
        ),
        FileSelectorItem(
            title = stringResource(id = R.string.file_selector_camera),
            icon = Icons.Rounded.CameraAlt,
            background = Color(0xFF347cff),
            action = notImplementedAction,
        ),
        FileSelectorItem(
            title = stringResource(id = R.string.file_selector_document),
            icon = Icons.Rounded.NoteAdd,
            background = Color(0xFF29cc80),
            action = notImplementedAction,
        ),
    )
    Box(modifier = modifier.padding(4.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEach { item ->
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = true)
                            .fillMaxHeight()
                            .clickable(onClick = item.action),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.tint,
                            modifier = Modifier
                                .size(56.dp)
                                .background(item.background, CircleShape)
                                .padding(12.dp),
                        )
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeApi::class)
@Composable
@Preview
private fun SelectorExpandedPreview() {
    SelectorExpanded(inputSelector = InputSelector.FILE, onFileSelected = {})
}

data class FileSelectorItem(
    val title: String,
    val icon: ImageVector,
    val background: Color,
    val tint: Color = Color.White,
    val action: () -> Unit = {},
)