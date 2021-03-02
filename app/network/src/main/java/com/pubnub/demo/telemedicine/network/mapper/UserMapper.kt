package com.pubnub.demo.telemedicine.network.mapper

import com.pubnub.demo.telemedicine.data.user.UserData
import com.pubnub.demo.telemedicine.initialization.data.User

/**
 * Mapper for transforming User object from UI to Db and vice versa
 */
object UserMapper {

    fun User.toDb(): UserData =
        UserData(
            id = id,
            name = name,
            profileUrl = profileUrl,
            custom = custom.toDb(),
        )

    fun UserData.toUi(): User =
        User(
            id = id,
            name = name,
            profileUrl = profileUrl,
            custom = custom.toUi(),
        )

    private fun User.Custom?.toDb(): UserData.CustomData? =
        if (this == null) null
        else UserData.CustomData(
            id = 0,
            username = username,
            title = title,
            isDoctor = isDoctor,
            birthday = birthday,
            email = email,
            sex = sex,
            specialization = specialization,
            hospital = hospital,
        )

    private fun UserData.CustomData?.toUi(): User.Custom? =
        if (this == null) null
        else User.Custom(
            username = username,
            title = title,
            isDoctor = isDoctor,
            birthday = birthday,
            email = email,
            sex = sex,
            specialization = specialization,
            hospital = hospital,
        )
}
