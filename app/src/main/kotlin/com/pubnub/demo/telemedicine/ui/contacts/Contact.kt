package com.pubnub.demo.telemedicine.ui.contacts

data class Contact(
    val id: String,
    val name: String,
    val imageUrl: String,
    val isDoctor: Boolean,
)