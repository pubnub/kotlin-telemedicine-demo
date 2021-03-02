package com.pubnub.demo.telemedicine.di.module

import android.content.res.Resources
import com.pubnub.demo.telemedicine.data.casestudy.CaseDao
import com.pubnub.demo.telemedicine.mapper.CaseMapperImpl
import com.pubnub.demo.telemedicine.mapper.PatientInfoMapperImpl
import com.pubnub.demo.telemedicine.network.service.CaseService
import com.pubnub.demo.telemedicine.network.service.MessageService
import com.pubnub.demo.telemedicine.repository.CaseRepository
import com.pubnub.demo.telemedicine.repository.CaseRepositoryImpl
import com.pubnub.demo.telemedicine.repository.MessageRepository
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.ui.conversation.Message
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
)
@Module
@InstallIn(SingletonComponent::class)
object CaseModule {
    @Provides
    @Singleton
    fun provideCaseRepository(
        caseDao: CaseDao,
        messageRepository: MessageRepository,
    ): CaseRepository =
        CaseRepositoryImpl(caseDao, messageRepository)

    @Provides
    @Singleton
    fun provideCaseService(
        caseRepository: CaseRepository,
    ): CaseService =
        CaseService(caseRepository)

    @Provides
    @Singleton
    fun provideCaseMapper(
        resources: Resources,
        userRepository: UserRepository,
        messageService: MessageService<Message>,
    ): CaseMapperImpl = CaseMapperImpl(
        resources, userRepository, messageService
    )

    @Provides
    @Singleton
    fun providePatientInfoMapper(
        resources: Resources,
        userRepository: UserRepository,
    ): PatientInfoMapperImpl = PatientInfoMapperImpl(resources, userRepository)
}
