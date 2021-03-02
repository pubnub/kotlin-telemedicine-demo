package com.pubnub.demo.telemedicine.initialization.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChannelGroup(
    @SerializedName("group")
    val group: String,
    @SerializedName("channels")
    val channels: List<String> = listOf(),
)
