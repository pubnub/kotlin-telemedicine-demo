package com.pubnub.demo.telemedicine.network.mapper

import com.pubnub.api.models.consumer.presence.PNHereNowChannelData
import com.pubnub.framework.data.Occupancy
import com.pubnub.framework.data.OccupancyMap
import com.pubnub.framework.mapper.OccupancyMapper
import java.util.HashMap

class OccupancyMapperImpl : OccupancyMapper {
    private fun mapEntry(entry: Map.Entry<String, PNHereNowChannelData>): Pair<String, Occupancy> =
        entry.key to Occupancy(
            entry.value.channelName,
            entry.value.occupancy,
            entry.value.occupants.map { it.uuid })

    override fun map(input: HashMap<String, PNHereNowChannelData>?): OccupancyMap? =
        input?.map { mapEntry(it) }?.toTypedArray()?.let {
            hashMapOf(*it)
        }
}