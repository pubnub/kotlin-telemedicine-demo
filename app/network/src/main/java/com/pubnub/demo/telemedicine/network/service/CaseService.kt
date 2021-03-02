package com.pubnub.demo.telemedicine.network.service

import com.pubnub.demo.telemedicine.data.casestudy.CaseData
import com.pubnub.demo.telemedicine.repository.CaseRepository
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Framework
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

/**
 * Case Service implementation
 *
 * The service is responsible for creating and managing cases.
 */
@OptIn(
    ExperimentalCoroutinesApi::class,
        FlowPreview::class,
)
@Framework
class CaseService(
    private val caseRepository: CaseRepository,
) {

    /**
     * Get list of Cases for passed user id
     * @param userId to get cases
     *
     * @return Flow with list of [CaseData]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAll(userId: UserId): Flow<List<CaseData>> =
        caseRepository.getAll(userId)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUnreadCount(userId: UserId): Flow<Long> =
        caseRepository.getUnreadCount(userId)
}
