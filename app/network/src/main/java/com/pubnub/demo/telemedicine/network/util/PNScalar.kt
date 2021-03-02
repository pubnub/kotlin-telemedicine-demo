package com.pubnub.demo.telemedicine.network.util

import timber.log.Timber

data class PNScalar(
    val data: String,
)

fun Any?.toScalar() =
    PNScalar(toJson())

fun Any?.asScalar() =
    toJson().fromJson<PNScalar>().also { Timber.i("Scalar: $it") }

inline fun <reified T> PNScalar.toObject(): T =
    data.fromJson()