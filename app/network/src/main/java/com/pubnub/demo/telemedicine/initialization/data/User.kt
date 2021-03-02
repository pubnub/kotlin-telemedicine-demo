package com.pubnub.demo.telemedicine.initialization.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class User(

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("profileUrl")
    val profileUrl: String? = null,

    @SerializedName("custom")
    val custom: Custom? = null,
) {
    @Keep
    data class Custom(

        @SerializedName("username")
        val username: String,

        // region Doctor
        @SerializedName("title")
        val title: String? = null,

        @SerializedName("doctor")
        val isDoctor: Boolean = false,

        @SerializedName("email")
        val email: String? = null,

        @SerializedName("specialization")
        val specialization: String? = null,

        @SerializedName("hospital")
        val hospital: String? = null,
        // endregion

        // region Patient
        @SerializedName("birthday")
        val birthday: Date? = null,

        @SerializedName("sex")
        val sex: String? = null,
        // endregion
    )

    fun isDoctor() = custom?.isDoctor == true
}