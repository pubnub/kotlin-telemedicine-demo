package com.pubnub.demo.telemedicine.initialization.data

import androidx.annotation.Keep

@Keep
data class EncryptedUser(
    val id: String,
    val name: String,
    val profileUrl: String? = null,
    val custom: Encrypted,
)

