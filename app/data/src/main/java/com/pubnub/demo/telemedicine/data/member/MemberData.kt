package com.pubnub.demo.telemedicine.data.member

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "member")
data class MemberData(
    @PrimaryKey
    val channel: String,
    val members: List<String>,
)
