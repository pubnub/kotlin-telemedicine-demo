package com.pubnub.demo.telemedicine.network.mapper

import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import com.pubnub.demo.telemedicine.data.message.MessageData
import com.pubnub.demo.telemedicine.network.util.fromJson
import com.pubnub.framework.mapper.Mapper

/**
 * Helper mapping [PNHistoryItemResult] into [MessageData]
 */
class HistoryMapperImpl :
    Mapper<PNHistoryItemResult, MessageData> {

    override fun map(input: PNHistoryItemResult): MessageData =
        input.toDb(timestamp = input.timetoken!!)

    private fun PNHistoryItemResult.toDb(
        isDelivered: Boolean = true,
        timestamp: Long,
    ): MessageData {
        val data = entry.toString().fromJson<MessageData>()
        return data.copy(
            isSent = isDelivered,
            timestamp = timestamp,
        )
    }
}
