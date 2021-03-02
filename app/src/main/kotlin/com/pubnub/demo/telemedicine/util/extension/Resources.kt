package com.pubnub.demo.telemedicine.util.extension

import android.content.res.Resources
import androidx.annotation.RawRes
import com.google.gson.Gson

inline fun <reified T> Resources.parseJson(@RawRes resourceId: Int): T =
    Gson().fromJson(
        openRawResource(resourceId).bufferedReader().use { it.readText() },
        T::class.java
    )
