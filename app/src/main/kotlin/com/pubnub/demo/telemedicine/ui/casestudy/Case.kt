package com.pubnub.demo.telemedicine.ui.casestudy

import com.pubnub.framework.util.Timetoken

data class Case(
    val id: String,
    val title: String,
    val description: String,
    val user: User,
    val lastMessage: Message?,
    val lastReadTimestamp: Timetoken,
    val notifications: Long,
    val closedTimestamp: Timetoken?,
    val closed: Boolean = closedTimestamp != null,
)

data class User(
    val id: String,
    val name: String,
    val age: String,
    val imageUrl: String,
)

data class Message(
    val content: String,
    val dateString: String,
)
