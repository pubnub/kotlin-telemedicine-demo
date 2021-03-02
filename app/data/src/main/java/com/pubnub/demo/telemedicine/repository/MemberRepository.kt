package com.pubnub.demo.telemedicine.repository

import com.pubnub.demo.telemedicine.data.member.MemberData

interface MemberRepository {
    suspend fun get(channelId: String): MemberData?
    suspend fun insert(channelId: String, memberIds: List<String>): MemberData
    suspend fun update(channelId: String, memberIds: List<String>): MemberData
    suspend fun insertOrUpdate(channelId: String, memberIds: List<String>): MemberData
    suspend fun delete(channelId: String)
}
