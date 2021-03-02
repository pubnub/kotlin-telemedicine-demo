package com.pubnub.demo.telemedicine.initialization.data

data class Occupant(
    val uuid: String,
    val userName: String,
    val title: String? = null,
    val imageUrl: String? = null,
    val online: Boolean = true,
)