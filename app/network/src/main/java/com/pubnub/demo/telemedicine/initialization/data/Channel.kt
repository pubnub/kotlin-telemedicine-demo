package com.pubnub.demo.telemedicine.initialization.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Channel(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("custom")
    val metadata: Metadata? = null,
) {

    @Keep
    data class Metadata(
        @SerializedName("type")
        val type: String,
        @SerializedName("createdAt")
        val createdAt: Long = System.currentTimeMillis() / 1_000L,
        @SerializedName("updatedAt")
        val updatedAt: Long = System.currentTimeMillis() / 1_000L,
        @SerializedName("closedAt")
        val closedAt: Long? = null,
        @SerializedName("readAt")
        val readAt: Long = 0L,
        @SerializedName("groups")
        val groups: List<String>? = null,
        @SerializedName("patient")
        val patient: String? = null,
        @SerializedName("doctors")
        val doctors: List<String>? = null,
    )
}
