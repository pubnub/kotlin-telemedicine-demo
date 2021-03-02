package com.pubnub.demo.telemedicine.initialization.data

data class Signal(
    val type: Type,
    val value: String,
) {
    enum class Type {
        CHANNEL,
        USER,
    }
}