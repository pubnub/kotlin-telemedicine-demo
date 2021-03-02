package com.pubnub.demo.telemedicine.ui.dashboard

import android.content.Context
import android.content.Intent
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pubnub.demo.telemedicine.ui.TelemedicineApplication
import com.pubnub.demo.telemedicine.ui.common.FeatureNotImplemented
import com.pubnub.demo.telemedicine.ui.login.LoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
class DrawerViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context
): ViewModel() {
    fun navigateToMyAccount(userId: String) {
        FeatureNotImplemented.toast(context)
    }

    fun navigateToHelpCenter() {
        FeatureNotImplemented.toast(context)
    }

    fun navigateToTerms() {
        FeatureNotImplemented.toast(context)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun logout() {
        (context as TelemedicineApplication).onLogout()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        context.startActivity(
            Intent(context, LoginActivity::class.java).apply {
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }
}