package com.pubnub.demo.telemedicine.di.module

import com.pubnub.demo.telemedicine.data.user.UserDao
import com.pubnub.demo.telemedicine.mapper.ContactMapperImpl
import com.pubnub.demo.telemedicine.network.mapper.MetadataUserMapperImpl
import com.pubnub.demo.telemedicine.network.service.UserService
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.repository.UserRepositoryImpl
import com.pubnub.demo.telemedicine.service.LoginService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository =
        UserRepositoryImpl(userDao)

    @Provides
    @Singleton
    fun provideUserService(
        userRepository: UserRepository,
        metadataUserMapperImpl: MetadataUserMapperImpl,
    ): UserService =
        UserService(userRepository, metadataUserMapperImpl)

    @Provides
    @Singleton
    fun provideLoginService(userRepository: UserRepository): LoginService =
        LoginService(userRepository)

    @Provides
    @Singleton
    fun provideContactMapper(): ContactMapperImpl = ContactMapperImpl()
}
