package com.pubnub.framework

import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.enums.PNLogVerbosity

// Should be moved to framework initialization
object PubNubFramework {

    private lateinit var pubNub: PubNub

    val userId get() = pubNub.configuration.uuid

    val instance
        get() =
            if (::pubNub.isInitialized) pubNub
            else throw PubNubNotInitializedException()

    /**
     * Initialize PubNub instance
     */
    fun initialize(
        publishKey: String,
        subscribeKey: String,
        userId: String,
        cipherKey: String? = null,
    ) {
        val config = PNConfiguration().apply {
            this.subscribeKey = subscribeKey
            this.publishKey = publishKey
            this.uuid = userId
            this.logVerbosity = PNLogVerbosity.BODY
            if (!cipherKey.isNullOrBlank())
                this.cipherKey = cipherKey
        }
        pubNub = PubNub(config)
    }
}