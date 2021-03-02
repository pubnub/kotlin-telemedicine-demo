package com.pubnub.demo.telemedicine.ui.doctor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.pubnub.demo.telemedicine.ui.common.LocalBackDispatcher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoctorInfoActivity : ComponentActivity() {

    companion object {
        const val USER_ID = "user_id"
    }

    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = intent.getStringExtra(USER_ID)!!

        setContent {
            CompositionLocalProvider(LocalBackDispatcher provides onBackPressedDispatcher) {
                DoctorInfoView(userId)
            }
        }
    }
}