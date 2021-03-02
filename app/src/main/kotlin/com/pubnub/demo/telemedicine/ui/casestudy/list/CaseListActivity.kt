package com.pubnub.demo.telemedicine.ui.casestudy.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pubnub.framework.PubNubFramework
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaseListActivity : ComponentActivity() {

    val userId get() = PubNubFramework.userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CasesView(userId)
        }
    }
}
