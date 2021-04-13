package com.pubnub.demo.telemedicine.network.service

import com.pubnub.demo.telemedicine.data.user.UserData
import com.pubnub.demo.telemedicine.initialization.data.User
import com.pubnub.demo.telemedicine.network.mapper.MetadataUserMapperImpl
import com.pubnub.demo.telemedicine.network.mapper.UserMapper.toDb
import com.pubnub.demo.telemedicine.network.util.toScalar
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Framework
import com.pubnub.framework.util.flow.coroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
@Framework
class UserService(
    private val userRepository: UserRepository,
    private val metadataUserMapperImpl: MetadataUserMapperImpl,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun get(userId: String? = null): UserData? {
        val metadata = PubNubFramework.instance.getUUIDMetadata(
            uuid = userId,
            includeCustom = true
        ).coroutine().data ?: return null

        return metadataUserMapperImpl.map(metadata)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun add(id: UserId, name: String, profileUrl: String?, custom: User.Custom): UserData? {
        val user = User(id, name, profileUrl, custom).toDb()
        val metadata = PubNubFramework.instance.setUUIDMetadata(
            uuid = user.id,
            name = user.name,
            profileUrl = user.profileUrl,
            custom = user.custom.toScalar(),
            includeCustom = true
        ).coroutine().data ?: return null

        return metadataUserMapperImpl.map(metadata)
    }


    suspend fun synchronizeNew(vararg userId: UserId) {
        val newUsers = userId.filter { !userRepository.has(it) } // take only not existing IDs
        synchronize(*newUsers.toTypedArray())
    }

    suspend fun synchronize(vararg userId: UserId) {
        Timber.i("Synchronize users")
        userId.forEachIndexed { index, id ->
            Timber.i("[${index + 1}/${userId.size}] User $id")
            try {
                get(id)?.run {
                    userRepository.insertOrUpdate(this)
                }
            } catch (e: Exception) {
                Timber.e(e, "Cannot synchronize user data: $id")
            }
        }
        Timber.i("Users synchronized")
    }
}
