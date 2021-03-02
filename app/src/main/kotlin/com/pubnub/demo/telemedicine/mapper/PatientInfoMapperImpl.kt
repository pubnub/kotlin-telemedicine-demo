package com.pubnub.demo.telemedicine.mapper

import android.content.res.Resources
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.data.casestudy.CaseData
import com.pubnub.demo.telemedicine.initialization.data.User
import com.pubnub.demo.telemedicine.network.mapper.UserMapper.toUi
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.ui.common.UserInfo
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.joda.time.Years

class PatientInfoMapperImpl(
    val resources: Resources,
    val userRepository: UserRepository,
) : Mapper<CaseData, UserInfo> {

    companion object {
        fun CaseData.toUi(
            resources: Resources,
            userResolver: (UserId) -> User,
        ): UserInfo {
            val user = userResolver(patientId)
            return UserInfo(
                userId = user.id,
                name = user.name,
                imageUrl = user.profileUrl!!,
                description = resources.getString(R.string.patient_description,
                    Years.yearsBetween(DateTime(user.custom!!.birthday), DateTime.now()).years,
                    name),
            )
        }
    }

    override fun map(input: CaseData): UserInfo =
        input.toUi(
            resources = resources,
            userResolver = {
                runBlocking {
                    userRepository.getUserByUuid(it)!!.toUi()
                }
            },
        )

}