package com.pubnub.demo.telemedicine.mapper

import android.content.res.Resources
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.data.user.UserData
import com.pubnub.demo.telemedicine.ui.common.UserInfo
import com.pubnub.framework.mapper.Mapper

class DoctorInfoMapperImpl(
    val resources: Resources,
) : Mapper<UserData, UserInfo> {

    companion object {
        fun UserData.toUi(
            resources: Resources,
        ): UserInfo {
            return UserInfo(
                userId = id,
                name = name,
                imageUrl = profileUrl!!,
                description = resources.getString(
                    R.string.physician_description,
                    custom?.specialization ?: "Unknown",
                    custom?.hospital ?: "Unknown",
                ),
            )
        }
    }

    override fun map(input: UserData): UserInfo =
        input.toUi(
            resources = resources
        )
}