package com.pubnub.framework.data

import androidx.annotation.Keep
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult

@Keep
data class Occupancy(
    val channel: String,
    val occupancy: Int,
    val list: List<String>? = null,
) {
    companion object {
        fun from(result: PNPresenceEventResult, occupants: List<String>? = null): Occupancy? =
            if (result.channel == null || result.occupancy == null) null
            else Occupancy(result.channel!!, result.occupancy!!, occupants)
    }
}

typealias OccupancyMap = HashMap<String, Occupancy>