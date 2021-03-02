package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.casestudy.CaseDao
import com.pubnub.demo.telemedicine.data.casestudy.CaseData
import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.demo.telemedicine.data.channel.ChannelType
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Case Repository Implementation
 *
 * Will manage cases in database
 */
@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
)
class CaseRepositoryImpl(
    private val caseDao: CaseDao,
    private val messageRepository: MessageRepository, // just for listening for new messages
) : CaseRepository {

    /**
     * Get Case by provided channel id
     * @param id of the case / channel
     *
     * @return [CaseData]
     */
    override suspend fun get(id: ChannelId): CaseData? =
        caseDao.get(id).first()

    /**
     * Get last Case for provided user
     *
     * @return [CaseData]
     */
    override suspend fun getLastForPatient(userId: String): CaseData? =
        caseDao.getLastByPatient(userId).firstOrNull()

    /**
     * Add new Case to the database
     * @param channelData to add
     * @param patientId which is concerned by the case
     * @param doctorId id or list of ids to connect to
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun add(
        channelData: ChannelData,
        patientId: UserId,
        vararg doctorId: UserId,
    ): CaseData {

        // create channel
        val case = channelData.copy(
            metadata = ChannelData.Metadata(
                type = ChannelType.CASE,
                patient = patientId,
                doctors = doctorId.toList(),
            )
        )


        // add files

        // return new instance
        return get(case.id)!!
    }

    /**
     * Get list of Cases for passed user id
     * @param userId to get cases
     *
     * Small workaround to get notified about cases changes added - @DatabaseView projection doesn't have listeners
     *
     * @return Flow with list of [CaseData] sorted descending by [CaseData.readAt]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAll(userId: UserId): Flow<List<CaseData>> =
        messageRepository.getAll() // just map for event
            .flatMapLatest { caseDao.getAll(userId) }
            .distinctUntilChanged()
            .map { list -> list.sortedByDescending { it.readAt } }

    /**
     * Set Case readAt timestamp
     */
    override suspend fun setReadAt(
        caseId: String,
        timestamp: Timetoken,
    ) {
        Timber.e("Set read on '$caseId'")
        caseDao.setReadAt(timestamp, caseId)
    }

    override fun getUnreadCount(userId: String): Flow<Long> =
        caseDao.getUnreadCount(userId).map { it ?: 0 }
}
