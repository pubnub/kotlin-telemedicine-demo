package com.pubnub.demo.telemedicine.util

import java.util.UUID
import kotlin.random.Random

fun Random.uuid(prefix: String? = null): String =
    (prefix?.plus("_") ?: "") + UUID.randomUUID().toString().replace("-", "")

fun Random.string(length: Int = 16): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}
