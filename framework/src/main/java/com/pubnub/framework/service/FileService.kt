package com.pubnub.framework.service

import com.pubnub.api.endpoints.remoteaction.RemoteAction
import com.pubnub.api.models.consumer.objects.PNPage
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Framework
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.FileInputStream
import kotlin.coroutines.resumeWithException

/**
 * File Service which handle files upload
 */
@FlowPreview
@OptIn(ExperimentalCoroutinesApi::class)
@Framework
class FileService {
    private val pubNub = PubNubFramework.instance

    suspend fun upload(channel: ChannelId, name: String, inputStream: FileInputStream, message: Any? = null, meta: Any? = null) =
        pubNub.sendFile(
            channel = channel,
            fileName = name,
            inputStream = inputStream,
            message = message,
            shouldStore = false,
            meta = meta,
            cipherKey = pubNub.configuration.cipherKey,
        ).coroutine()

    suspend fun list(channel: ChannelId, limit: Int? = null, next: PNPage.PNNext? = null) =
        pubNub.listFiles(channel, limit, next).coroutine()

    suspend fun getUrl(channel: ChannelId, fileName: String, fileId: String) =
        pubNub.getFileUrl(channel, fileName, fileId).coroutine()
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <Output> RemoteAction<Output>.coroutine(
    onCancellation: ((Throwable) -> Unit)? = null,
): Output =
    suspendCancellableCoroutine { continuation ->
        try {
            continuation.resume(sync()!!, onCancellation)
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
