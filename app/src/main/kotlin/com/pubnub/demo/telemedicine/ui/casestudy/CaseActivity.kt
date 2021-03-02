package com.pubnub.demo.telemedicine.ui.casestudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import com.pubnub.demo.telemedicine.repository.CaseRepository
import com.pubnub.demo.telemedicine.ui.casestudy.info.CaseView
import com.pubnub.demo.telemedicine.ui.common.LocalBackDispatcher
import com.pubnub.framework.PubNubFramework
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ObsoleteCoroutinesApi
@OptIn(ExperimentalCoroutinesApi::class)
@FlowPreview
@AndroidEntryPoint
class CaseActivity : ComponentActivity() {

    companion object {
        const val CASE_ID = "case_id"
    }

    @Inject
    lateinit var caseRepository: CaseRepository

    lateinit var caseId: String

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        caseId = intent.getStringExtra(CASE_ID)!!

        setContent {
            CompositionLocalProvider(LocalBackDispatcher provides onBackPressedDispatcher) {
                CaseView(caseId, PubNubFramework.userId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            caseRepository.setReadAt(caseId)
        }
    }

    override fun onPause() {
        super.onPause()
        GlobalScope.launch(Dispatchers.IO) {
            caseRepository.setReadAt(caseId)
        }
    }
}
