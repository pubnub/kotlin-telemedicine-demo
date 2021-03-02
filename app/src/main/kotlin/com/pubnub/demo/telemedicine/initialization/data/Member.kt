package com.pubnub.demo.telemedicine.initialization.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Member(
    @SerializedName("members")
    val members: List<String>,
    @SerializedName("channel")
    val channel: String,
)
