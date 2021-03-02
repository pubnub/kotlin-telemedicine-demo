package com.pubnub.demo.telemedicine.data.message

import androidx.annotation.Keep
import androidx.annotation.StringDef
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

@Keep
@Entity(tableName = "message_receipt")
data class ReceiptData(

    val channelId: ChannelId,

    val userId: UserId,

    val messageTimestamp: Long,

    val actionTimestamp: Long,

    val action: String,

    @PrimaryKey
    val id: String = "$userId-$channelId-$messageTimestamp-$actionTimestamp",
) {
    object Type {
        const val DELIVERED = "message_delivered"
        const val READ = "message_read"
    }
}

@StringDef(
    value = [
        ReceiptData.Type.DELIVERED,
        ReceiptData.Type.READ,
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class ReceiptTypeDef
