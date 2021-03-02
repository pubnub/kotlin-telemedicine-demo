package com.pubnub.demo.telemedicine.mapper

import android.content.res.Resources
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.data.chat.ChatData
import com.pubnub.demo.telemedicine.initialization.data.User
import com.pubnub.demo.telemedicine.network.mapper.UserMapper.toUi
import com.pubnub.demo.telemedicine.network.service.MessageService
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.ui.conversation.Chat
import com.pubnub.demo.telemedicine.ui.conversation.Message
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.joda.time.Years

/**
 * Mapper for transforming Chat object from UI to Db and vice versa
 */
class ChatMapperImpl(
    val resources: Resources,
    val userRepository: UserRepository,
    val messageService: MessageService<Message>,
    val type: String = GROUP_CHAT,
) : Mapper<ChatData, Chat> {

    private val currentUserId: UserId get() = PubNubFramework.userId
    private val showUser: Boolean = type == GROUP_CHAT

    companion object {

        const val GROUP_CHAT = "GROUP_CHAT"
        const val SINGLE_CHAT = "SINGLE_CHAT"

        fun ChatData.toUi(
            resources: Resources,
            currentUserId: UserId,
            userResolver: (UserId) -> User,
            messageResolver: (ChannelId) -> Message?,
            withUser: Boolean = true,
        ): Chat {
            val oppositeUserId = getOppositeUserId(currentUserId)
            val user = userResolver(oppositeUserId)

            val userDescription =
                if (user.isDoctor()) resources.getString(R.string.physician_description,
                    user.custom?.specialization ?: "Unknown",
                    user.custom?.hospital ?: "Unknown"
                )
                else resources.getString(R.string.patient_description,
                    Years.yearsBetween(DateTime(user.custom!!.birthday), DateTime.now()).years,
                    name
                )

            return Chat(
                id = id,
                title = if (user.isDoctor()) resources.getString(R.string.physician_name,
                    user.name) else user.name,
                description = userDescription,
                user = CaseMapperImpl.getUser(resources, user),
                lastMessage = messageResolver(id)?.run {
                    CaseMapperImpl.getMessage(resources,
                        this,
                        if (withUser) userResolver(this.authorId) else null)
                },
                lastReadTimestamp = readAt,
                notifications = unreadCount,
                closedTimestamp = null,
                isOnline = false
            )
        }

        fun ChatData.getOppositeUserId(currentUserId: UserId): UserId =
            listOfNotNull(patient, *doctors.toTypedArray()).first { it != currentUserId }

    }

    override fun map(input: ChatData): Chat =
        input.toUi(
            resources = resources,
            currentUserId = currentUserId,
            userResolver = {
                runBlocking {
                    userRepository.getUserByUuid(it)!!.toUi()
                }
            },
            messageResolver = {
                runBlocking {
                    if (showUser) {
                        messageService.getLast(it, 1).firstOrNull()?.firstOrNull()
                    } else {
                        val oppositeUserId = input.getOppositeUserId(currentUserId)
                        messageService.getLast(it, 1).firstOrNull()?.firstOrNull()
                    }
                }
            },
            withUser = showUser,
        )
}
