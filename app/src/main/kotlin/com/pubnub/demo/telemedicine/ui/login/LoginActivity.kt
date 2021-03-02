package com.pubnub.demo.telemedicine.ui.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi

@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
@ObsoleteCoroutinesApi
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val loginViewModel: LoginViewModel = viewModel()
            loginViewModel.reset()

            val isDoctor = remember { mutableStateOf(true) }
            val randomUser =
                remember { mutableStateOf(loginViewModel.getRandomUser(isDoctor = isDoctor.value).custom!!.username) }
            val randomPassword = remember { mutableStateOf(loginViewModel.getRandomPassword()) }

            // debug only
            IconButton(onClick = {
                randomUser.value =
                    loginViewModel.getRandomUser(isDoctor = isDoctor.value).custom!!.username
                randomPassword.value = loginViewModel.getRandomPassword()
            }) {
                Icon(Icons.Filled.Refresh, null)
            }

            LoginView(
                user = randomUser.value,
                password = randomPassword.value,
                isDoctor = isDoctor,
                onLoginCallback = { login, password ->
                    loginViewModel.login(this,
                        isDoctor.value,
                        login,
                        password)
                },
                loginErrorState = loginViewModel.loginErrorState,
                passwordErrorState = loginViewModel.passwordErrorState,
            )
        }
    }
}
