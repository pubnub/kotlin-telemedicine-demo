package com.pubnub.demo.telemedicine.mapper

import android.content.res.Resources
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.data.casestudy.CaseData
import com.pubnub.demo.telemedicine.initialization.data.User
import com.pubnub.demo.telemedicine.network.mapper.UserMapper.toUi
import com.pubnub.demo.telemedicine.network.service.MessageService
import com.pubnub.demo.telemedicine.repository.UserRepository
import com.pubnub.demo.telemedicine.ui.casestudy.Case
import com.pubnub.demo.telemedicine.ui.conversation.Message
import com.pubnub.demo.telemedicine.util.extension.isToday
import com.pubnub.demo.telemedicine.util.extension.isYesterday
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.seconds
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.joda.time.Years
import timber.log.Timber

class CaseMapperImpl(
    val resources: Resources,
    val userRepository: UserRepository,
    val messageService: MessageService<Message>,
) : Mapper<CaseData, Case> {

    companion object {
        fun CaseData.toUi(
            resources: Resources,
            userResolver: (UserId) -> User,
            messageResolver: (ChannelId) -> Message?,
        ): Case {
            val user = userResolver(patientId)
            return Case(
                id = id,
                title = name,
                description = resources.getString(R.string.case_description,
                    user.name,
                    Years.yearsBetween(DateTime(user.custom!!.birthday), DateTime.now()).years),
                user = getUser(resources, user),
                lastMessage = messageResolver(id)?.run {
                    getMessage(resources, this, userResolver(this.authorId))
                },
                lastReadTimestamp = readAt,
                notifications = unreadCount,
                closedTimestamp = closedAt,
            )
        }

        fun getUser(resources: Resources, user: User) =
            com.pubnub.demo.telemedicine.ui.casestudy.User(
                id = user.id,
                name = user.name,
                age = resources.getString(R.string.case_user_age,
                    Years.yearsBetween(DateTime(user.custom!!.birthday), DateTime.now()).years),
                imageUrl = user.profileUrl ?: ""
            )

        fun getMessage(
            resources: Resources,
            message: Message,
            user: User? = null,
        ): com.pubnub.demo.telemedicine.ui.casestudy.Message {
            val userName = when {
                user == null -> null
                user.isDoctor() -> "*${resources.getString(R.string.physician_name, user.name)}*"
                else -> "*${user.name}*"
            }

            return com.pubnub.demo.telemedicine.ui.casestudy.Message(
                content = arrayOf(userName, message.content).filterNotNull().joinToString(" "),
                dateString = getDateString(resources, message.timestamp)
            )
        }


        private fun getDateString(resources: Resources, timestamp: Timetoken): String {
            val date = DateTime(timestamp.seconds)
            return when {
                date.isToday() -> date.toString(resources.getString(R.string.case_date_format_today))
                    .toLowerCase()
                date.isYesterday() -> resources.getString(R.string.case_date_format_yesterday)
                else -> date.toString(resources.getString(R.string.case_date_format_before_yesterday))
            }
        }
    }

    override fun map(input: CaseData): Case =
        input.toUi(
            resources = resources,
            userResolver = {
                runBlocking {
                    Timber.i("Get user: $it")
                    userRepository.getUserByUuid(it)!!.toUi()
                }
            },
            messageResolver = {
                runBlocking {
                    messageService.getLast(it, 1).firstOrNull()?.firstOrNull()
                }
            }
        )
}