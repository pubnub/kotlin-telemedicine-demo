package com.pubnub.demo.telemedicine.ui.conversation

import com.pubnub.demo.telemedicine.ui.casestudy.Message
import com.pubnub.demo.telemedicine.ui.casestudy.User
import com.pubnub.framework.util.Timetoken

data class Chat(
    val id: String,
    val title: String,
    val description: String,
    val user: User,
    val lastMessage: Message?,
    val lastReadTimestamp: Timetoken,
    val notifications: Long,
    val closedTimestamp: Timetoken?,
    val isOnline: Boolean,
)