package com.pubnub.demo.telemedicine.data.channel

import androidx.annotation.StringDef

object ChannelType {
    const val TEAM = "team"
    const val DOCTOR = "doctor"
    const val CASE = "case"
    const val PATIENT = "patient"
    const val CHAT = "chat"
    val ALL = arrayOf(CASE, DOCTOR, TEAM, PATIENT, CHAT)
}

@StringDef(
    value = [
        ChannelType.TEAM,
        ChannelType.DOCTOR,
        ChannelType.CASE,
        ChannelType.PATIENT,
        ChannelType.CHAT,
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class ChannelTypeDef
