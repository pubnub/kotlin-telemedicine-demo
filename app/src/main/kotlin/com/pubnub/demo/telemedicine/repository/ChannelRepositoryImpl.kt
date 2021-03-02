package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.channel.ChannelDao
import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.demo.telemedicine.data.channel.ChannelType
import com.pubnub.demo.telemedicine.data.channel.ChannelTypeDef
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import kotlin.math.max

class ChannelRepositoryImpl(
    private val channelDao: ChannelDao,
    private val memberRepository: MemberRepository,
    private val groupRepository: ChannelGroupRepository,
) : ChannelRepository {

    override suspend fun has(id: String): Boolean =
        get(id) != null

    override suspend fun get(id: String): ChannelData? =
        channelDao.get(id)

    override suspend fun add(
        channel: ChannelData,
    ): ChannelData {

        insertOrUpdate(channel)

        val members = listOfNotNull(
            *channel.metadata?.doctors?.toTypedArray() ?: emptyArray(),
            channel.metadata?.patient,
        )

        memberRepository.insertOrUpdate(channel.id, members)

        return channel
    }

    override suspend fun remove(id: String) {
        channelDao.delete(id)
        memberRepository.delete(id)
    }

    override suspend fun setReadAt(channelId: String, timestamp: Timetoken) {
        channelDao.setReadAt(timestamp, channelId)
    }

    override fun getByType(
        userId: UserId,
        @ChannelTypeDef type: String,
        filter: (ChannelData) -> Boolean,
    ): Flow<List<ChannelData>> =
        channelDao.getAllByType(userId, type).map { list ->
            list.filter { filter.invoke(it) }
        }

    override fun getDoctorChannel(doctorId: String): Flow<ChannelData?> =
        channelDao.getAllByDoctor(doctorId, ChannelType.DOCTOR).map { it.firstOrNull() }

    override suspend fun getConversation(userId: UserId, contactId: UserId): ChannelData? =
        channelDao.getConversation(userId, contactId)

    override fun getPatientChannel(patientId: String): Flow<ChannelData?> =
        channelDao.getAllByPatient(patientId, ChannelType.PATIENT).map {
            Timber.e("Get Patient Channel: ${it.joinToString(", ")}")
            it.firstOrNull()
        }

    private suspend fun insertOrUpdate(channel: ChannelData) {
        val prevData = channelDao.get(channel.id)
        if (prevData != null) {
            // don't override readAt - todo: fix it later to base on message_receipt
            val metadata = channel.metadata?.copy(readAt = max(prevData.metadata?.readAt ?: 0,
                channel.metadata?.readAt ?: 0))
            channelDao.update(channel.copy(metadata = metadata))
        } else channelDao.insert(channel)
    }
}
