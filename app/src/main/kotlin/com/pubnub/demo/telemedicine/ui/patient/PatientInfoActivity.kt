package com.pubnub.demo.telemedicine.ui.patient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.pubnub.demo.telemedicine.ui.common.LocalBackDispatcher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientInfoActivity : ComponentActivity() {

    companion object {
        const val CASE_ID = "case_id"
    }

    lateinit var caseId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        caseId = intent.getStringExtra(CASE_ID)!!

        setContent {
            CompositionLocalProvider(LocalBackDispatcher provides onBackPressedDispatcher) {
                PatientInfoView(caseId)
            }
        }
    }
}