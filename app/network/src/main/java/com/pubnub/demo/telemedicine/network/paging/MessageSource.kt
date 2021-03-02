package com.pubnub.demo.telemedicine.network.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.pubnub.demo.telemedicine.data.message.MessageWithActions
import com.pubnub.demo.telemedicine.network.service.MessageService
import com.pubnub.demo.telemedicine.repository.MessageRepository
import com.pubnub.framework.data.ChannelId
import timber.log.Timber
import java.io.IOException

@ExperimentalPagingApi
class MessageRemoteMediator<T : Any>(
    private val channelId: ChannelId,
    private val service: MessageService<T>,
    private val messageRepository: MessageRepository,
) : RemoteMediator<Int, MessageWithActions>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageWithActions>,
    ): MediatorResult {
        Timber.e("Remote load: $loadType")
        val page = when (loadType) {
            LoadType.REFRESH -> {
                getTimeWindowForInitMessage(state)
            }
            LoadType.PREPEND -> {
                // load new messages at the end - reverse layout!
                getTimeWindowForFirstMessage(state)
            }
            LoadType.APPEND -> {
                // load new messages at start of database - reverse layout
                getTimeWindowForLastMessage(state)
            }
        }

        try {
            if (page == null)
                return MediatorResult.Success(endOfPaginationReached = false)

            loadNewMessages(channelId, page.start, page.end)

            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getTimeWindowForInitMessage(state: PagingState<Int, MessageWithActions>): MessageWindow? {

        if (messageRepository.getLast(channelId) != null)
            return null

        // Get the history
        val start = System.currentTimeMillis() * 10_000L
        val end = 1L

        return MessageWindow(channelId, start, end)
    }

    private suspend fun getTimeWindowForFirstMessage(state: PagingState<Int, MessageWithActions>): MessageWindow? {

        val message = state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
        return message?.let {
            if (messageRepository.hasMoreAfter(channelId, it.timestamp)) null
            else {
                // Get the history
                val start = System.currentTimeMillis() * 10_000L
                val end = it.timestamp + 1

                MessageWindow(channelId, start, end)
            }
        }
    }

    private suspend fun getTimeWindowForLastMessage(state: PagingState<Int, MessageWithActions>): MessageWindow? {
        val message = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()

        return message?.let {
            if (messageRepository.hasMoreBefore(channelId, it.timestamp)) null
            else {
                // Get the history
                val start = it.timestamp - 1
                val end = 1L
                MessageWindow(channelId, start, end)
            }
        }
    }

    private suspend fun loadNewMessages(channelId: ChannelId, start: Long?, end: Long?) {
        // Get the history
        service.getHistory(
            channelId = channelId,
            start = start,
            end = end,
            count = 100,
        )
    }
}

private data class MessageWindow(
    val channelId: ChannelId,
    val start: Long?,
    val end: Long?,
)