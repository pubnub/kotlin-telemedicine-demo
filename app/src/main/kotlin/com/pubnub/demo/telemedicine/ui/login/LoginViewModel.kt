package com.pubnub.demo.telemedicine.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.service.LoginService
import com.pubnub.demo.telemedicine.ui.TelemedicineApplication
import com.pubnub.demo.telemedicine.ui.dashboard.DashboardActivity
import com.pubnub.demo.telemedicine.util.string
import com.pubnub.framework.ui.component.login.IncorrectPasswordException
import com.pubnub.framework.ui.component.login.UserNotFoundException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
@FlowPreview
class LoginViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val service: LoginService,
    private val userRepository: UserRepository,
) : ViewModel() {

    lateinit var loginErrorState: MutableState<Throwable?>
    lateinit var passwordErrorState: MutableState<Throwable?>

    @Composable
    fun reset() {
        loginErrorState = mutableStateOf(null)
        passwordErrorState = mutableStateOf(null)
    }

    fun getRandomUser(isDoctor: Boolean) =
        runBlocking { if (isDoctor) userRepository.getRandomDoctor() else userRepository.getRandomPatient() }


    fun getRandomPassword() =
        Random.string(16)


    fun login(activity: Activity, isDoctor: Boolean, login: String, password: String) {
        MainScope().launch(Dispatchers.IO) {
            try {
                val user = service.login(login, password, isDoctor)
                loginCompleted(activity, user.id)
            } catch (e: Exception) {
                when (e) {
                    is UserNotFoundException -> {
                        loginErrorState.value = e
                    }
                    is IncorrectPasswordException -> {
                        passwordErrorState.value = e
                    }
                }
                loginError(e)
            }
        }
    }

    private fun loginCompleted(activity: Activity, userId: String) {
        Timber.i("Login completed with user '$userId'")

        val application = context as TelemedicineApplication
        application.onLogin(userId)

        activity.startActivity(Intent(activity, DashboardActivity::class.java))
        activity.finish()
    }

    private fun loginError(throwable: Throwable) {
        Timber.w("Login error: $throwable")
    }
}
