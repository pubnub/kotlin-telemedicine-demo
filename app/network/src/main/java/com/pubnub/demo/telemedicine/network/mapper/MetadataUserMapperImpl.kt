package com.pubnub.demo.telemedicine.network.mapper

import com.pubnub.api.models.consumer.objects.uuid.PNUUIDMetadata
import com.pubnub.demo.telemedicine.data.user.UserData
import com.pubnub.demo.telemedicine.network.util.asScalar
import com.pubnub.demo.telemedicine.network.util.toObject
import com.pubnub.framework.mapper.Mapper

/**
 * Helper mapping [PNUUIDMetadata] into [UserData]
 */
class MetadataUserMapperImpl : Mapper<PNUUIDMetadata, UserData> {

    override fun map(input: PNUUIDMetadata): UserData =
        input.toUserData()

    fun PNUUIDMetadata.toUserData(): UserData =
        UserData(
            id = this.id,
            name = this.name!!,
            profileUrl = this.profileUrl,
            custom = this.custom.asScalar().toObject(),
        )
}
