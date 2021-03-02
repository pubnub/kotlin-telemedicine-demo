package com.pubnub.demo.telemedicine.ui.dashboard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.pubnub.demo.telemedicine.R

sealed class NavigationScreens(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val iconRes: Int,
) {
    object Cases : NavigationScreens("Cases", R.string.navigation_cases, R.drawable.ic_cases)
    object PatientChats :
        NavigationScreens("Patient Chats", R.string.navigation_chats, R.drawable.ic_chats)

    object DoctorChats :
        NavigationScreens("Doctor Chats", R.string.navigation_chats, R.drawable.ic_chats)

    object Contacts :
        NavigationScreens("Contacts", R.string.navigation_contacts, R.drawable.ic_contacts)

    object TestResult :
        NavigationScreens("Test Result", R.string.navigation_test_result, R.drawable.ic_result)

    object Prescription : NavigationScreens("Prescription",
        R.string.navigation_prescription,
        R.drawable.ic_prescription)
}