package com.pubnub.demo.telemedicine.mapper

import com.pubnub.demo.telemedicine.data.user.UserData
import com.pubnub.demo.telemedicine.ui.contacts.Contact
import com.pubnub.framework.mapper.Mapper

/**
 * Mapper for transforming UserData object to Contact
 */
class ContactMapperImpl : Mapper<UserData, Contact> {

    private fun UserData.toContact(): Contact =
        Contact(
            id = this.id,
            name = this.name,
            imageUrl = this.profileUrl ?: "",
            isDoctor = this.isDoctor(),
        )

    override fun map(input: UserData): Contact =
        input.toContact()
}
