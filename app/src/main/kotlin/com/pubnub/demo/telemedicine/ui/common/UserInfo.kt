package com.pubnub.demo.telemedicine.ui.common

import com.pubnub.framework.data.UserId

data class UserInfo(
    val userId: UserId,
    val name: String,
    val description: String,
    val imageUrl: String,
)