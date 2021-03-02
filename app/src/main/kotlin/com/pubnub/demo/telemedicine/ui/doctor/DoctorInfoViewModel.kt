package com.pubnub.demo.telemedicine.ui.doctor

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pubnub.demo.telemedicine.mapper.DoctorInfoMapperImpl
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.ui.common.UserInfo
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.runBlocking

class DoctorInfoViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val doctorInfoMapper: DoctorInfoMapperImpl,
) : ViewModel() {

    fun getInfo(userId: UserId): UserInfo? =
        runBlocking {
            userRepository.getUserByUuid(userId)?.let {
                doctorInfoMapper.map(it)
            }
        }
}
