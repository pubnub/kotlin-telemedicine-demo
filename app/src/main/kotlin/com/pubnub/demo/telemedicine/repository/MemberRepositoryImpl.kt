package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.member.MemberDao
import com.pubnub.demo.telemedicine.data.member.MemberData
import com.pubnub.framework.data.ChannelId

class MemberRepositoryImpl(
    private val memberDao: MemberDao,
) : MemberRepository {

    override suspend fun get(channelId: String): MemberData? =
        memberDao.get(channelId)

    override suspend fun insert(channelId: String, memberIds: List<String>): MemberData =
        MemberData(channelId, memberIds).also { member ->
            memberDao.insert(member)
        }

    override suspend fun update(channelId: String, memberIds: List<String>): MemberData =
        MemberData(channelId, memberIds).also { member ->
            memberDao.update(member)
        }

    override suspend fun insertOrUpdate(channelId: String, memberIds: List<String>): MemberData =
        MemberData(channelId, memberIds).also { member ->
            if (memberDao.get(member.channel) != null) memberDao.update(member)
            else memberDao.insert()
        }

    override suspend fun delete(channelId: ChannelId) =
        memberDao.delete(channelId)
}
