package com.pubnub.demo.telemedicine.ui.prescription

data class Prescription(
    val title: String,
    val physicianName: String,
    val physicianImage: String,
    val dosage: String,
    val date: String,
)
