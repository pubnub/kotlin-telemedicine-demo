package com.pubnub.demo.telemedicine.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.ui.Background
import com.pubnub.framework.ui.component.login.Login
import com.pubnub.framework.ui.component.login.LoginType

@Composable
fun LoginView(
    user: String = "",
    password: String = "",
    isDoctor: MutableState<Boolean> = remember { mutableStateOf(true) },
    onLoginCallback: (String, String) -> Unit,
    loginErrorState: State<Throwable?> = mutableStateOf(null),
    passwordErrorState: State<Throwable?> = mutableStateOf(null),
) {

    Background { constraints ->

        LoginComponent(
            user = user,
            password = password,

            isDoctor = isDoctor,
            onLoginCallback = onLoginCallback,

            loginErrorState = loginErrorState,
            passwordErrorState = passwordErrorState,
            constraints = constraints,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginComponent(
    user: String = "",
    password: String = "",
    isDoctor: MutableState<Boolean>,
    onLoginCallback: (String, String) -> Unit,
    loginErrorState: State<Throwable?>,
    passwordErrorState: State<Throwable?>,
    constraints: BoxWithConstraintsScope,
) {
    val width = with(LocalDensity.current) { constraints.maxWidth }
    val height = with(LocalDensity.current) { constraints.maxHeight }

    val padding = min(width, height) * 0.1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Logo()
        Spacer(modifier = Modifier.height(64.dp))
        Login(
            login = user,
            password = password,
            isDoctor = isDoctor,
            type = LoginType.LoginPassword,
            onLogin = onLoginCallback,
            loginErrorState = loginErrorState,
            passwordErrorState = passwordErrorState,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun Logo(
    asset: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_logo),
    modifier: Modifier = Modifier,
) {
    Image(
        imageVector = asset,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth(0.4f)
            .clipToBounds(),
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginView(onLoginCallback = { _, _ -> })
}
