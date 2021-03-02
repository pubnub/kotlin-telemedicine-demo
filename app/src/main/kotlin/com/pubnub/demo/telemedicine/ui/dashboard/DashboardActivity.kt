package com.pubnub.demo.telemedicine.ui.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.network.service.SynchronizationService
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.UserId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ObsoleteCoroutinesApi
@OptIn(ExperimentalCoroutinesApi::class)
@FlowPreview
@AndroidEntryPoint
class DashboardActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var synchronizationService: SynchronizationService

    private val userId: UserId get() = PubNubFramework.userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        synchronizationService.start()

        val user = runBlocking { userRepository.getUserByUuid(userId) }
            ?: throw Exception("User not found")

        setContent {

            val viewModel: DashboardViewModel = viewModel()

            DashboardView(
                userId = userId,
                userName = if (user.isDoctor()) resources.getString(R.string.physician_name,
                    user.name) else user.name,
                profileUrl = user.profileUrl,
                dashboardViewModel = viewModel,
            )
        }
    }
}
