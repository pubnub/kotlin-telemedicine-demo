package com.pubnub.demo.telemedicine

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pubnub.demo.telemedicine.converter.DateConverter
import com.pubnub.demo.telemedicine.converter.StringListConverter
import com.pubnub.demo.telemedicine.data.casestudy.CaseDao
import com.pubnub.demo.telemedicine.data.casestudy.CaseData
import com.pubnub.demo.telemedicine.data.channel.ChannelDao
import com.pubnub.demo.telemedicine.data.channel.ChannelData
import com.pubnub.demo.telemedicine.data.channel.ChannelGroupDao
import com.pubnub.demo.telemedicine.data.channel.ChannelGroupData
import com.pubnub.demo.telemedicine.data.chat.ChatDao
import com.pubnub.demo.telemedicine.data.chat.ChatData
import com.pubnub.demo.telemedicine.data.member.MemberDao
import com.pubnub.demo.telemedicine.data.member.MemberData
import com.pubnub.demo.telemedicine.data.message.MessageDao
import com.pubnub.demo.telemedicine.data.message.MessageData
import com.pubnub.demo.telemedicine.data.message.ReceiptDao
import com.pubnub.demo.telemedicine.data.message.ReceiptData
import com.pubnub.demo.telemedicine.data.user.UserDao
import com.pubnub.demo.telemedicine.data.user.UserData

@Database(
    entities = [
        UserData::class, UserData.CustomData::class,
        MessageData::class, MessageData.CustomData::class,
        ReceiptData::class,
        ChannelData::class, ChannelData.Metadata::class,
        ChannelGroupData::class,
        MemberData::class,
    ],
    views = [
        CaseData::class,
        ChatData::class,
    ],
    version = 1,
)
@TypeConverters(DateConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun channelDao(): ChannelDao
    abstract fun channelGroupDao(): ChannelGroupDao
    abstract fun caseDao(): CaseDao
    abstract fun chatDao(): ChatDao
    abstract fun memberDao(): MemberDao
}
