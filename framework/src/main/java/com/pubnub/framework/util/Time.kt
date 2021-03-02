package com.pubnub.framework.util

typealias Timetoken = Long

val Timetoken.seconds: Seconds
    get() = this / 10_000L

typealias Seconds = Long

val Seconds.timetoken: Timetoken
    get() = this * 10_000L
