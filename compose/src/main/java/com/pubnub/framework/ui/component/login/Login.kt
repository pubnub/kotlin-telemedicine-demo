package com.pubnub.framework.ui.component.login

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pubnub.framework.ui.R
import com.pubnub.framework.ui.component.login.button.LoginButton
import com.pubnub.framework.ui.icon.Visibility
import com.pubnub.framework.ui.icon.VisibilityOff
import com.pubnub.framework.ui.theme.red
import com.pubnub.framework.util.KeyboardAllActions
import timber.log.Timber

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MaterialTheme {
        Login(
            onLogin = { _, _ -> }
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun Login(
    login: String = "",
    password: String = "",

    isDoctor: MutableState<Boolean> = remember { mutableStateOf(true) },
    loginErrorState: State<Throwable?> = remember { mutableStateOf(null) },
    passwordErrorState: State<Throwable?> = remember { mutableStateOf(null) },

    onLogin: (String, String) -> Unit,
    type: LoginType = LoginType.LoginPassword,
    modifier: Modifier = Modifier.wrapContentSize(),
) {

    val loginState: MutableState<TextFieldValue> =
        remember(login) { mutableStateOf(TextFieldValue(login)) }
    val passwordState: MutableState<TextFieldValue> =
        remember(password) { mutableStateOf(TextFieldValue(password)) }


    val loginAction = {
        onLogin(loginState.value.text, passwordState.value.text)
    }
    val actionPerformed: (ImeAction, SoftwareKeyboardController?) -> Unit =
        { action, softwareController ->
            Timber.e("Action $action")
            if (action == ImeAction.Done) {
                softwareController?.hideSoftwareKeyboard()
                onLogin(login, password)
            }
        }

    InnerLogin(
        modifier = modifier,
        type = type,
        action = loginAction,
        actionPerformed = actionPerformed,
        loginState = loginState,
        loginErrorState = loginErrorState,
        passwordState = passwordState,
        passwordErrorState = passwordErrorState,
        isDoctor = isDoctor,
    )

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InnerLogin(
    modifier: Modifier = Modifier,
    type: LoginType = LoginType.LoginPassword,
    action: () -> Unit,
    actionPerformed: (ImeAction, SoftwareKeyboardController?) -> Unit,
    loginState: MutableState<TextFieldValue>,
    loginErrorState: State<Throwable?>,
    passwordState: MutableState<TextFieldValue>,
    passwordErrorState: State<Throwable?>,
    isDoctor: MutableState<Boolean>,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        val loginFieldAction = if (!type.passwordVisible()) actionPerformed else null
        LoginField(type, loginState, loginErrorState, loginFieldAction)

        Spacer(modifier = Modifier.height(16.dp))

        if (type.passwordVisible())
            PasswordField(passwordState, passwordErrorState, actionPerformed)

        Spacer(modifier = Modifier.height(24.dp))

        UserTypeSelector(modifier = Modifier.fillMaxWidth(), isSelected = isDoctor)

        Spacer(modifier = Modifier.height(32.dp))

        LoginButton(onClick = action)
    }
}

@Composable
private fun UserTypeSelector(
    modifier: Modifier = Modifier,
    isSelected: MutableState<Boolean>,
) {
    val label = stringResource(id = R.string.login_user_type_hint)
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isSelected.value, onCheckedChange = { isSelected.value = it })
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label,
            fontSize = 12.sp,
            color = LocalContentColor.current.copy(alpha = 0.4f))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginField(
    type: LoginType,
    state: MutableState<TextFieldValue>,
    errorState: State<Throwable?>,
    actionPerformed: ((ImeAction, SoftwareKeyboardController?) -> Unit)? = null,
) {
    val label =
        stringResource(id = if (type.isLogin()) R.string.login_field_hint else R.string.email_field_hint)
    val keyboardType = if (type.isLogin()) KeyboardType.Text else KeyboardType.Email
    val imeAction = if (type.passwordVisible()) ImeAction.Next else ImeAction.Done
    val keyboard = KeyboardOptions(
        keyboardType = keyboardType,
        imeAction = imeAction,
    )

    InputField(
        label = label,
        state = state,
        errorState = errorState,
        keyboard = keyboard,
        actionPerformed = actionPerformed ?: { _, _ -> },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PasswordField(
    state: MutableState<TextFieldValue>,
    errorState: State<Throwable?>,
    actionPerformed: ((ImeAction, SoftwareKeyboardController?) -> Unit),
) {
    val label = stringResource(id = R.string.password_field_hint)
    val keyboard = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done,
    )

    val showPassword = remember { mutableStateOf(false) }

    InputField(
        label = label,
        state = state,
        errorState = errorState,
        keyboard = keyboard,
        trailingIcon = {
            if (state.value.text.isNotBlank())
                ShowPasswordIcon(state = showPassword)
        },
        actionPerformed = actionPerformed,
        transformation = PasswordTransformation(showPassword = showPassword),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ShowPasswordIcon(state: MutableState<Boolean>) {
    val modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onPress = {
            state.value = true
            awaitRelease()
            state.value = false
        })
    }


    if (state.value) Icon(imageVector = Icons.Filled.Visibility, contentDescription = null, modifier = modifier)
    else Icon(imageVector = Icons.Filled.VisibilityOff, contentDescription = null, modifier = modifier)
}

@Composable
private fun PasswordTransformation(showPassword: MutableState<Boolean>) =
    if (showPassword.value) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputField(
    label: String,
    state: MutableState<TextFieldValue>,
    errorState: State<Throwable?> = mutableStateOf(null),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    actionPerformed: (ImeAction, SoftwareKeyboardController?) -> Unit = { _, _ -> },
    keyboard: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    transformation: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier.wrapContentSize(),
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
        TextField(
            value = state.value,
            onValueChange = {
                state.value = it
            },
            label = { InputLabel(label) },
            isError = errorState.value != null,
            keyboardOptions = keyboard,
            visualTransformation = transformation,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            modifier = modifier,
            keyboardActions = KeyboardAllActions { action ->
                actionPerformed.invoke(action, softwareKeyboardController)
            },
//            keyboardActions = { action, softwareController ->
//                actionPerformed.invoke(action, softwareController)
//            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                errorCursorColor = red,
                errorIndicatorColor = red,
                errorLabelColor = red,
                errorLeadingIconColor = red,
                errorTrailingIconColor = red,
            ),
            shape = MaterialTheme.shapes.large,
        )

    val textColor = MaterialTheme.colors.error

    if (errorState.value?.message != null)
        Row {
            // just to move error to text position
            Spacer(Modifier.width(16.dp))
            Text(
                text = errorState.value?.message ?: "",
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.caption.copy(color = textColor),
                modifier = modifier,
            )
        }
}

@Composable
private fun InputLabel(label: String) {
    Text(label)
}

@Composable
@Preview
private fun UserTypeSelectorDoctorPreview() {
    UserTypeSelector(isSelected = mutableStateOf(true))
}

@Composable
@Preview
private fun UserTypeSelectorPatientPreview() {
    UserTypeSelector(isSelected = mutableStateOf(false))
}
