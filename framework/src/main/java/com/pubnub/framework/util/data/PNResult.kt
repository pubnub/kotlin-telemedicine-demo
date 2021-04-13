package com.pubnub.framework.util.data

import com.pubnub.api.models.consumer.PNStatus

/**
 * Result data class
 *
 * @param result response object
 * @param status of request
 */
data class PNResult<Output>(
    val result: Output?,
    val status: PNStatus
)