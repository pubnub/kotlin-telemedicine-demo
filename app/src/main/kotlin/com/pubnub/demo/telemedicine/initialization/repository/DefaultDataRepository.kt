package com.pubnub.demo.telemedicine.initialization.repository

import android.content.res.Resources
import androidx.annotation.RawRes
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.initialization.data.Channel
import com.pubnub.demo.telemedicine.initialization.data.ChannelGroup
import com.pubnub.demo.telemedicine.initialization.data.User
import com.pubnub.demo.telemedicine.util.extension.parseJson

internal class DefaultDataRepository(private val resources: Resources) {

    // region Default Data
    val users: Array<User> = arrayOf(
        *parseArray(R.raw.users_patient),
        *parseArray(R.raw.users_physician),
    )

    val channels: List<Channel> = mutableListOf(
        *parseArray(R.raw.channels_chat),
        *parseArray(R.raw.channels_case),
        *parseArray(R.raw.channels_patient_holder),
        *parseArray(R.raw.channels_physician_holder),
    )

    val channelGroups: List<ChannelGroup> = mutableListOf(
        *parseArray(R.raw.channel_groups_patient),
        *parseArray(R.raw.channel_groups_physician),
    )
    // endregion

    private inline fun <reified T> parseArray(@RawRes resource: Int): Array<out T> =
        resources.parseJson<Array<T>>(resource)
}

