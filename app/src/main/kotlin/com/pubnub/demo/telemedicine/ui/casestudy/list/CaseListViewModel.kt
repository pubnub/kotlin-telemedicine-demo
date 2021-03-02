package com.pubnub.demo.telemedicine.ui.casestudy.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pubnub.demo.telemedicine.initialization.data.User
import com.pubnub.demo.telemedicine.mapper.CaseMapperImpl
import com.pubnub.demo.telemedicine.network.mapper.UserMapper.toUi
import com.pubnub.demo.telemedicine.network.service.CaseService
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.ui.casestudy.Case
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CaseListViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val caseService: CaseService,
    private val caseMapper: CaseMapperImpl,
) : ViewModel() {

    // region User
    fun getUsername(userId: UserId): String =
        getUser(userId).name

    fun getUser(userId: UserId): User =
        runBlocking { userRepository.getUserByUuid(userId)!!.toUi() }
    // endregion

    // region Case
    @Composable
    fun getCases(userId: UserId): List<Case> {
        val cases: List<Case> by caseService.getAll(userId)
            .map { list -> list.map { caseMapper.map(it) } }.collectAsState(initial = listOf())
        return cases
    }
    // endregion
}