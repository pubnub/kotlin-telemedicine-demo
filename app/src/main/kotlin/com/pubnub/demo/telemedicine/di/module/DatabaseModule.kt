package com.pubnub.demo.telemedicine.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pubnub.demo.telemedicine.AppDatabase
import com.pubnub.demo.telemedicine.data.casestudy.CaseDao
import com.pubnub.demo.telemedicine.data.channel.ChannelDao
import com.pubnub.demo.telemedicine.data.channel.ChannelGroupDao
import com.pubnub.demo.telemedicine.data.chat.ChatDao
import com.pubnub.demo.telemedicine.data.member.MemberDao
import com.pubnub.demo.telemedicine.data.message.MessageDao
import com.pubnub.demo.telemedicine.data.message.ReceiptDao
import com.pubnub.demo.telemedicine.data.user.UserDao
import com.pubnub.demo.telemedicine.initialization.repository.DefaultDataRepository
import com.pubnub.demo.telemedicine.mapper.ChannelGroupMapper.toDb
import com.pubnub.demo.telemedicine.mapper.ChannelMapper.toDb
import com.pubnub.demo.telemedicine.network.mapper.UserMapper.toDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "database"

    private var INSTANCE: AppDatabase? = null

    // region Database
    @Singleton
    @Provides
    @Synchronized
    fun prepareDatabase(
        @ApplicationContext applicationContext: Context,
    ): AppDatabase =
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .prepopulate(applicationContext)
            .build()
            .also {
                Timber.e("Create instance: $it")
                INSTANCE = it
            }
    // endregion

    // Region DAO
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao =
        database.userDao()

    @Provides
    @Singleton
    fun provideMessageDao(database: AppDatabase): MessageDao =
        database.messageDao()

    @Provides
    @Singleton
    fun provideReceiptDao(database: AppDatabase): ReceiptDao =
        database.receiptDao()

    @Provides
    @Singleton
    fun provideChannelDao(database: AppDatabase): ChannelDao =
        database.channelDao()

    @Provides
    @Singleton
    fun provideChannelGroupDao(database: AppDatabase): ChannelGroupDao =
        database.channelGroupDao()

    @Provides
    @Singleton
    fun provideCaseDao(database: AppDatabase): CaseDao =
        database.caseDao()

    @Provides
    @Singleton
    fun provideChatDao(database: AppDatabase): ChatDao =
        database.chatDao()

    @Provides
    @Singleton
    fun provideMemberDao(database: AppDatabase): MemberDao =
        database.memberDao()
    // endregion

    // region Prepopulate
    private fun <T : RoomDatabase> RoomDatabase.Builder<T>.prepopulate(context: Context): RoomDatabase.Builder<T> =
        addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    val defaultDataRepository = DefaultDataRepository(context.resources)

                    // insert the data on the IO Thread
                    GlobalScope.launch(Dispatchers.IO) {
                        with(INSTANCE!!) {

                            // add users
                            val users = defaultDataRepository.users.map { it.toDb() }.toTypedArray()
                            userDao().insert(*users)

                            val channels =
                                defaultDataRepository.channels.map { it.toDb() }.toTypedArray()
                            channelDao().insert(*channels)

                            val groups =
                                defaultDataRepository.channelGroups.map { it.toDb() }.toTypedArray()
                            channelGroupDao().insert(*groups)
                        }
                    }
                }
            }
        )
    // endregion
}
