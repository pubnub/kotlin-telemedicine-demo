package com.pubnub.demo.telemedicine.ui.dashboard

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pubnub.demo.telemedicine.data.casestudy.CaseData
import com.pubnub.demo.telemedicine.data.channel.ChannelTypeDef
import com.pubnub.demo.telemedicine.initialization.data.User
import com.pubnub.demo.telemedicine.mapper.CaseMapperImpl
import com.pubnub.demo.telemedicine.mapper.ChatMapperImpl
import com.pubnub.demo.telemedicine.mapper.ContactMapperImpl
import com.pubnub.demo.telemedicine.network.mapper.UserMapper.toUi
import com.pubnub.demo.telemedicine.network.service.CaseService
import com.pubnub.demo.telemedicine.network.service.ChannelService
import com.pubnub.demo.telemedicine.network.service.MessageService
import com.pubnub.demo.telemedicine.repository.ChatRepository
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.ui.casestudy.Case
import com.pubnub.demo.telemedicine.ui.casestudy.CaseActivity
import com.pubnub.demo.telemedicine.ui.casestudy.list.CaseListActivity
import com.pubnub.demo.telemedicine.ui.common.FeatureNotImplemented
import com.pubnub.demo.telemedicine.ui.contacts.Contact
import com.pubnub.demo.telemedicine.ui.conversation.Chat
import com.pubnub.demo.telemedicine.ui.conversation.ChatActivity
import com.pubnub.demo.telemedicine.ui.conversation.Message
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.OccupancyService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Named

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
@FlowPreview
class DashboardViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val contactMapper: ContactMapperImpl,
    private val caseService: CaseService,
    private val caseMapper: CaseMapperImpl,
    private val chatRepository: ChatRepository, // todo: add chat service?
    @Named(ChatMapperImpl.SINGLE_CHAT) private val singleChatMapper: ChatMapperImpl,
    private val messageService: MessageService<Message>,
    private val occupancyService: OccupancyService,
    private val channelService: ChannelService,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    init {
        Timber.e("Initialized: $this")
    }

    // region User
    fun getUsername(userId: UserId): String =
        getUser(userId)?.name ?: "Unknown user"

    fun getUser(userId: UserId): User? =
        runBlocking { userRepository.getUserByUuid(userId)?.toUi() }
    // endregion

    // region Case
    @Composable
    fun getCases(userId: UserId): List<Case> {
        val cases by caseService.getAll(userId)
            .map { list ->
                list.map { caseMapper.map(it) }
            }.collectAsState(initial = listOf())
        return cases
    }

    @Composable
    fun getUnreadCaseMessagesCount(userId: UserId): Long {
        val count by caseService.getUnreadCount(userId).collectAsState(initial = 0L)
        return count
    }
    // endregion

    // region Chat
    @Composable
    fun getPatientChats(userId: UserId): List<Chat> {
        val result: List<Chat> by occupancyService.getOnline()
            .combine(chatRepository.getPatientChats(userId)) { online, chats ->
                chats.map {
                    val chat = singleChatMapper.map(it)
                    chat.copy(isOnline = online.contains(chat.user.id))
                }
            }.collectAsState(initial = listOf())

        return result
    }

    @Composable
    fun getDoctorChats(userId: UserId): List<Chat> {
        val result: List<Chat> by occupancyService.getOnline()
            .combine(chatRepository.getDoctorChats(userId)) { online, chats ->
                chats.map {
                    val chat = singleChatMapper.map(it)
                    chat.copy(isOnline = online.contains(chat.user.id))
                }
            }.collectAsState(initial = listOf())

        return result
    }

    @Composable
    fun getUnreadChatMessagesCount(userId: UserId): Long {
        val count by chatRepository.getUnreadCount(userId).collectAsState(initial = 0L)
        return count
    }
    // endregion

    // region Contacts
    @Composable
    fun getContacts(userId: UserId): List<Contact> {
        val contacts by userRepository.getContacts(userId)
            .map { list -> list.map { contactMapper.map(it) } }
            .collectAsState(initial = listOf())
        return contacts
    }
    // endregion

    // region Navigation
    fun navigateToCase(id: String) {
        context.startActivity(
            Intent(context, CaseActivity::class.java).apply {
                putExtra(CaseActivity.CASE_ID, id)
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun navigateToConversation(channelId: ChannelId, @ChannelTypeDef channelType: String) {
        context.startActivity(
            Intent(context, ChatActivity::class.java).apply {
                putExtra(ChatActivity.CHANNEL_ID, channelId)
                putExtra(ChatActivity.CHANNEL_TYPE, channelType)
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun navigateToCaseList() {
        context.startActivity(
            Intent(context, CaseListActivity::class.java).apply {
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun navigateToCaseCreator() {
        FeatureNotImplemented.toast(context)
    }
    // endregion

    // region Messages
    /**
     * Listen for incoming messages
     * Subscribe to all the cases
     */
    fun listenForMessages(cases: Flow<List<CaseData>>) {
        GlobalScope.launch(Dispatchers.IO) {
            messageService.bind()
            cases.onEach { channelList ->
                val channels = channelList.map { it.id }.toTypedArray()
                Timber.e("New channels: ${channels.joinToString(", ")}")
                occupancyService.setChannels(*channels)
            }.launchIn(this)
        }
    }

    fun listenForMessages() {
        Timber.i("-> Listen for messages")
        GlobalScope.launch(Dispatchers.IO) {
            messageService.bind()
            channelService.getUserChannelGroupIds().onEach { channelGroupList ->
                val channelGroups = channelGroupList.toTypedArray()
                Timber.e("New channelGroups: ${channelGroups.joinToString(", ")}")
                occupancyService.setChannelGroups(*channelGroups)
            }.launchIn(this)
        }
    }

    /**
     * Listen for incoming messages
     * Subscribe to all the cases
     */
    fun stopListenForMessages() {
        Timber.i("<- Stop listening for messages")
        GlobalScope.launch {
            messageService.unbind()
            occupancyService.removeAllChannels()
        }
    }
    // endregion
}