package com.pubnub.demo.telemedicine.mapper

import android.content.res.Resources
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.UserIdMapper
import kotlinx.coroutines.runBlocking

/**
 * Mapper for transforming User UUID into name
 */
class UserIdMapperImpl(
    private val resources: Resources,
    private val userRepository: UserRepository,
) : UserIdMapper {

    private val userNameResolver: (UserId) -> String = { userId ->
        val user = runBlocking { userRepository.getUserByUuid(userId) }
        when {
            user == null -> "Unknown"
            user.isDoctor() -> resources.getString(R.string.physician_name, user.name)
            else -> user.name
        }
    }

    override fun map(input: UserId): String =
        userNameResolver(input)
}