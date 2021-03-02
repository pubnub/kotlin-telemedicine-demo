package com.pubnub.framework.data

import com.pubnub.framework.util.timetoken

data class Typing(
    val userId: UserId,
    val channelId: ChannelId,
    val isTyping: Boolean,
    val timestamp: Long = System.currentTimeMillis().timetoken,
)

typealias TypingMap = HashMap<UserId, Typing>