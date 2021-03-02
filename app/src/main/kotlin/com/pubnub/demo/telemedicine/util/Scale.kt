package com.pubnub.demo.telemedicine.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import kotlin.math.max
import kotlin.math.min

@Stable
val CenterInside = object : ContentScale {

    override fun computeScaleFactor(srcSize: Size, dstSize: Size): ScaleFactor {
        return if (srcSize.width <= dstSize.width &&
            srcSize.height <= dstSize.height
        ) {
            ScaleFactor(1.0f, 1.0f)
        } else {
            computeFillMaxDimension(srcSize, dstSize).let {
                ScaleFactor(it, it)
            }
        }
    }
}

internal fun computeFillMaxDimension(srcSize: Size, dstSize: Size): Float {
    val widthScale = computeFillWidth(srcSize, dstSize)
    val heightScale = computeFillHeight(srcSize, dstSize)
    return max(widthScale, heightScale)
}

internal fun computeFillMinDimension(srcSize: Size, dstSize: Size): Float {
    val widthScale = computeFillWidth(srcSize, dstSize)
    val heightScale = computeFillHeight(srcSize, dstSize)
    return min(widthScale, heightScale)
}

internal fun computeFillWidth(srcSize: Size, dstSize: Size): Float =
    dstSize.width / srcSize.width

internal fun computeFillHeight(srcSize: Size, dstSize: Size): Float =
    dstSize.height / srcSize.height
