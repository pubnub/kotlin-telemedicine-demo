package com.pubnub.demo.telemedicine.mapper

import android.content.res.Resources
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.data.message.MessageData
import com.pubnub.demo.telemedicine.data.message.MessageWithActions
import com.pubnub.demo.telemedicine.ui.conversation.Message
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.mapper.UserAvatarMapper
import com.pubnub.framework.mapper.UserIdMapper
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.seconds
import org.joda.time.DateTime
import java.util.UUID

/**
 * Mapper for transforming Message object from UI to Db and vice versa
 */
class MessageWithActionsMapperImpl(
    val resources: Resources,
    private val userIdMapper: UserIdMapper,
    private val userAvatarMapper: UserAvatarMapper,
) : Mapper<MessageWithActions, Message> {

    companion object {
        fun Message.toDb(
            userId: String,
            channelId: String,
            isDelivered: Boolean = this.isDelivered,
        ): MessageData =
            MessageData(
                id = UUID.randomUUID().toString(),
                userId = userId,
                channelId = channelId,
                isSent = isDelivered,
                timestamp = timestamp,
                custom = MessageData.CustomData(content = content, image = image),
            )

        fun MessageWithActions.toUi(
            resources: Resources,
            authorNameResolver: (UserId) -> String,
            profileAvatarResolver: (UserId) -> String?,
        ): Message =
            Message(
                id = id,
                author = authorNameResolver(userId),
                authorId = userId,
                content = custom.content,
                timestamp = timestamp,
                date = getDateString(resources, timestamp),
                avatarUrl = profileAvatarResolver(userId),
                image = custom.image,
                isDelivered = isSent,
            )

        private fun getDateString(resources: Resources, timestamp: Timetoken): String {
            val date = DateTime(timestamp.seconds)
            return date.toString(resources.getString(R.string.case_date_format_today))
                .toLowerCase()
        }
    }

    override fun map(input: MessageWithActions): Message =
        input.toUi(
            resources = resources,
            authorNameResolver = { userId -> userIdMapper.map(userId) },
            profileAvatarResolver = { userId -> userAvatarMapper.map(userId) },
        )
}
