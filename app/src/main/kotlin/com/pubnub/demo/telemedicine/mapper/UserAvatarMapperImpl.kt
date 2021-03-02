package com.pubnub.demo.telemedicine.mapper

import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.UserAvatarMapper
import kotlinx.coroutines.runBlocking

/**
 * Mapper for transforming User UUID into name
 */
class UserAvatarMapperImpl(private val userRepository: UserRepository) : UserAvatarMapper {

    private val userAvatarResolver: (UserId) -> String? = { userId ->
        runBlocking { userRepository.getUserByUuid(userId) }?.profileUrl
    }

    override fun map(input: UserId): String? =
        userAvatarResolver(input)
}