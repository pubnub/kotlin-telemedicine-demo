package com.pubnub.demo.telemedicine.ui.common

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object FeatureNotImplemented {

    fun toast(context: Context) {
        Toast.makeText(context, "Feature not implemented", Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun show() {
        toast(LocalContext.current)
    }
}