package com.pubnub.demo.telemedicine.initialization.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.pubnub.demo.telemedicine.data.message.MessageData

@Keep
data class DefaultData(
    @SerializedName("members")
    val members: List<Member>,
    @SerializedName("channels")
    val channels: List<Channel>,
    @SerializedName("channelGroups")
    val channelGroups: List<ChannelGroup>,
    @SerializedName("users")
    val users: List<User>,
    @SerializedName("messages")
    val messages: List<MessageData>,
)
