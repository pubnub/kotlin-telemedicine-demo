package com.pubnub.demo.telemedicine.initialization.data

import androidx.annotation.Keep

@Keep
data class EncryptedChannel(
    val id: String,
    val name: String,
    val description: String,
    var metadata: Encrypted?,
)

