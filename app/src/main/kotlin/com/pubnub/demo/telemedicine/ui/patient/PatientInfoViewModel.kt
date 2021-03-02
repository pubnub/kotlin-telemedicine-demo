package com.pubnub.demo.telemedicine.ui.patient

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pubnub.demo.telemedicine.mapper.PatientInfoMapperImpl
import com.pubnub.demo.telemedicine.repository.CaseRepository
import com.pubnub.demo.telemedicine.ui.common.UserInfo
import com.pubnub.framework.data.ChannelId
import kotlinx.coroutines.runBlocking

class PatientInfoViewModel @ViewModelInject constructor(
    private val caseRepository: CaseRepository,
    private val patientInfoMapper: PatientInfoMapperImpl,
) : ViewModel() {

    fun getInfo(caseId: ChannelId): UserInfo? =
        runBlocking {
            caseRepository.get(caseId)?.let {
                patientInfoMapper.map(it)
            }
        }
}
