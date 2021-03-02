package com.pubnub.demo.telemedicine.util.coil

import android.content.Context
import coil.bitmap.BitmapPool
import coil.decode.DecodeResult
import coil.decode.Options
import coil.size.Size
import com.pubnub.api.PubNub
import okio.BufferedSource
import okio.buffer
import okio.source

class PubNubEncryptedImageDecoder(val context: Context, val pubNub: PubNub) :
    BitmapFactoryDecoder(context) {

    override suspend fun decode(
        pool: BitmapPool,
        source: BufferedSource,
        size: Size,
        options: Options,
    ): DecodeResult {
        if (!isCipherKeyValid())
            return super.decode(pool, source, size, options)

        val decryptedStream =
            pubNub.decryptInputStream(source.inputStream(), pubNub.configuration.cipherKey)
        val bufferedSource: BufferedSource = decryptedStream.source().buffer()
        return super.decode(pool, bufferedSource, size, options)
    }

    private fun isCipherKeyValid() = try {
        pubNub.configuration.cipherKey.isNotBlank()
    } catch (e: Exception) {
        false
    }
}