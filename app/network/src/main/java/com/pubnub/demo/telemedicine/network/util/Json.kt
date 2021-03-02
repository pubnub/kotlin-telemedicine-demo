package com.pubnub.demo.telemedicine.network.util

import com.pubnub.framework.PubNubFramework

fun Any?.toJson() =
    PubNubFramework.instance.mapper.toJson(this)

inline fun <reified T> String?.fromJson(): T =
    PubNubFramework.instance.mapper.fromJson(this, T::class.java)
