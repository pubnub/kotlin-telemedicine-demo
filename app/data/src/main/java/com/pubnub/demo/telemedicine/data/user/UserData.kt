package com.pubnub.demo.telemedicine.data.user

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity(tableName = "user")
data class UserData(

    @PrimaryKey
    val id: String,

    val name: String,

    val profileUrl: String?,

    @Embedded(prefix = "custom_")
    val custom: CustomData? = null,
) {
    @Keep
    @Entity(tableName = "user_custom")
    data class CustomData(

        @PrimaryKey(autoGenerate = true)
        val id: Int,

        val username: String,
        // region Doctor
        val title: String? = null,

        val isDoctor: Boolean = false,

        val email: String? = null,

        val specialization: String? = null,

        val hospital: String? = null,
        // endregion

        // region Patient
        val birthday: Date? = null,

        val sex: String? = null,
        // endregion
    )

    fun isDoctor() = custom?.isDoctor == true
}
