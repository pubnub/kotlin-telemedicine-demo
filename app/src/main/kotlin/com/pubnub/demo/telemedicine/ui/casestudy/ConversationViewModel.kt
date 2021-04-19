package com.pubnub.demo.telemedicine.ui.casestudy

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.pubnub.demo.telemedicine.data.message.ReceiptData
import com.pubnub.demo.telemedicine.mapper.CaseMapperImpl
import com.pubnub.demo.telemedicine.mapper.ChatMapperImpl
import com.pubnub.demo.telemedicine.mapper.MessageMapperImpl.Companion.toDb
import com.pubnub.demo.telemedicine.network.service.MessageService
import com.pubnub.demo.telemedicine.network.service.ReceiptService
import com.pubnub.demo.telemedicine.repository.CaseRepository
import com.pubnub.demo.telemedicine.repository.ChatRepository
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.ui.conversation.Chat
import com.pubnub.demo.telemedicine.ui.conversation.ConversationUiState
import com.pubnub.demo.telemedicine.ui.conversation.Message
import com.pubnub.demo.telemedicine.ui.doctor.DoctorInfoActivity
import com.pubnub.demo.telemedicine.ui.patient.PatientInfoActivity
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.FileService
import com.pubnub.framework.service.OccupancyService
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.timetoken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.FileInputStream
import javax.inject.Named

@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
class ConversationViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val chatRepository: ChatRepository,
    private val caseRepository: CaseRepository,
    private val userRepository: UserRepository,
    private val messageService: MessageService<Message>,
    private val receiptService: ReceiptService,
    private val occupancyService: OccupancyService,
    private val fileService: FileService,
    private val caseMapper: CaseMapperImpl,
    @Named(ChatMapperImpl.SINGLE_CHAT) private val chatMapper: ChatMapperImpl,
) : ViewModel() {
    val time: Timetoken
        get() = System.currentTimeMillis().timetoken

    // region State
    private val userId: UserId get() = PubNubFramework.userId

    private val authorMe by lazy { runBlocking { userRepository.getUserByUuid(userId)!! } }

    @Composable
    fun getState(
        channelId: String,
        channelName: String,
        channelDescription: String,
    ): ConversationUiState {

        val lastRead = getLastRead(channelId = channelId, userId = userId)
            .collectAsState(initial = 0L)
        val lastConfirmed = getLastConfirmed(channelId = channelId, userId = userId)
            .collectAsState(initial = 0L)
        val messages = getMessages(channelId = channelId)

        val occupancy = occupancyService.getOccupancy(channelId).map { it.list ?: emptyList() }
            .collectAsState(initial = emptyList())

        return remember {
            ConversationUiState(
                messages = messages,
                channelId = channelId,
                channelName = channelName,
                channelDescription = channelDescription,
                lastReadTimestamp = lastRead,
                lastConfirmedTimestamp = lastConfirmed,
                occupants = occupancy
            )
        }
    }
    // endregion

    // region Case
    fun getCase(caseId: String): Case? =
        runBlocking {
            caseRepository.get(caseId)?.let {
                caseMapper.map(it)
            }
        }

    fun getLastCase(userId: UserId): Case? =
        runBlocking {
            caseRepository.getLastForPatient(userId)?.let {
                caseMapper.map(it)
            }
        }

    // endregion

    // region Channel
    @Composable
    fun getChat(channelId: ChannelId): Chat {
        val chat = runBlocking {
            chatRepository.get(channelId)?.let {
                chatMapper.map(it)
            }
        }!!

        val online by occupancyService.isOnline(chat.user.id).collectAsState(initial = false)
        return chat.copy(isOnline = online)
    }
    // endregion

    // region Messages
    fun getMessages(channelId: String): Flow<PagingData<Message>> =
        messageService.getPage(channelId)

    fun sendMessage(caseId: String, message: String, fileUri: Uri? = null) {
        GlobalScope.launch(Dispatchers.IO) {
            val fileUrl = if (fileUri == null) null else uploadFile(caseId, fileUri)

            val messageData = createMessage(content = message, fileUrl = fileUrl).toDb(
                userId = userId,
                channelId = caseId,
                isDelivered = false,
            )
            messageService.send(caseId, messageData)
        }
    }

    fun sendFile(caseId: String, fileUri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            // TODO: 2/2/21 add message to database with local url to show image faster on list
            val fileUrl = uploadFile(caseId, fileUri)
            Timber.e("File url: $fileUrl")
            val messageData = createMessage(content = "", fileUrl = fileUrl).toDb(
                userId = userId,
                channelId = caseId,
                isDelivered = false,
            )
            messageService.send(caseId, messageData)
        }
    }

    private suspend fun uploadFile(caseId: String, uri: Uri): String {
        // send file
        val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")!!.fileDescriptor
        val name = context.contentResolver.getFileName(uri)!!
        val result = fileService.upload(
            channel = caseId,
            name = name,
            inputStream = FileInputStream(fileDescriptor),
        )

        Timber.i("Result: $result")
        return fileService.getUrl(caseId, result.file.name, result.file.id).url
    }


    private fun ContentResolver.getFileName(uri: Uri): String? {

        val cursor: Cursor? = query(
            uri, null, null, null, null, null)

        return cursor?.use {
            if (it.moveToFirst())
                it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            else
                null
        }
    }

    fun createMessage(content: String, fileUrl: String? = null, timetoken: Long = time): Message =
        Message(author = authorMe.name,
            authorId = userId,
            content = content,
            image = fileUrl,
            timestamp = timetoken)

    suspend fun pullHistory(
        channelId: String,
        start: Long? = System.currentTimeMillis() * 10_000L,
        end: Long? = messageService.getLastTimestamp(channelId) + 1,
        count: Int = 100,
    ) = messageService.getHistory(channelId, start, end, count)
    // endregion

    // region Receipts
    fun getLastRead(channelId: String, userId: UserId): Flow<Long> =
        runBlocking { receiptService.getLastRead(channelId, userId) }

    fun getLastConfirmed(channelId: String, userId: UserId): Flow<Long> =
        runBlocking { receiptService.getLastConfirmed(channelId, userId) }

    fun navigateToPatientInfo(caseId: ChannelId) {
        context.startActivity(
            Intent(context, PatientInfoActivity::class.java).apply {
                putExtra(PatientInfoActivity.CASE_ID, caseId)
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun navigateToProfile(userId: UserId) {
        context.startActivity(
            Intent(context, DoctorInfoActivity::class.java).apply {
                putExtra(DoctorInfoActivity.USER_ID, userId)
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }
    //
}
