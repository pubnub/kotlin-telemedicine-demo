package com.pubnub.demo.telemedicine.ui.prescription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pubnub.demo.telemedicine.ui.chat.UserProfileImage
import com.pubnub.demo.telemedicine.ui.conversation.messageFormatter
import com.pubnub.demo.telemedicine.ui.dashboard.DateLabel
import com.pubnub.demo.telemedicine.ui.dashboard.Description
import com.pubnub.demo.telemedicine.ui.dashboard.Label
import com.pubnub.demo.telemedicine.ui.dashboard.LabelSmall

private val DividerModifier = Modifier.padding(95.dp, 0.dp, 20.dp, 0.dp)
private val CardModifier = Modifier.padding(20.dp)

private val mockedPrescriptions = listOf(
    Prescription(
        title = "Amoxicillin 250mg",
        physicianName = "Dr. Alexander Wolfe",
        dosage = "One to be taken three times a day after food",
        physicianImage = "https://github.com/pubnub/kotlin-telemedicine-demo/blob/master/setup/users/portrait_smiling_handsome_male_doctor_man-abe6a9adfc322800a12e75590503ef09-6cd8a2.png",
        date = "8:12am",
    ),
    Prescription(
        title = "Omeprazole 10mg + 2 more",
        physicianName = "Dr. Casey Blume",
        dosage = "One a day after food",
        physicianImage = "https://uc.uxpin.com/files/879252/879907/confident_middle_aged_woman_doctor_wearing_medical_robe_and_stethoscope_standing_in_profile_view_on_isolated_orange_wall_with_copy_space-0d97acaca7e90a9daa4b656602f8dbbc-397645.png",
        date = "YESTERDAY",
    ),
    Prescription(
        title = "Acetaminophen 300mg",
        physicianName = "Dr. Casey Blume",
        dosage = "One to be taken twice a day after food",
        physicianImage = "https://uc.uxpin.com/files/879252/879907/confident_middle_aged_woman_doctor_wearing_medical_robe_and_stethoscope_standing_in_profile_view_on_isolated_orange_wall_with_copy_space-0d97acaca7e90a9daa4b656602f8dbbc-397645.png",
        date = "2/8/21",
    ),
)

@Composable
fun PrescriptionList(chats: List<Prescription>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(chats) { index, item ->
            PrescriptionCard(
                imageUrl = item.physicianImage,
                title = item.title,
                description = item.physicianName,
                content = item.dosage,
                changeTime = item.date,
                modifier = Modifier.then(CardModifier)
            )
            if (index < chats.size - 1)
                Divider(modifier = DividerModifier)
        }
    }
}

@Composable
fun PrescriptionCard(
    title: String,
    description: String,
    content: String,
    imageUrl: String? = "https://github.com/pubnub/kotlin-telemedicine-demo/blob/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png",
    changeTime: String = "09:47 AM",
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        UserProfileImage(
            modifier = Modifier.size(55.dp),
            imageUrl = imageUrl,
            isOnline = false,
            indicatorSize = 16.dp
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Row {
                Label(text = title, modifier = Modifier.weight(1f))
                DateLabel(text = changeTime)
            }
            Spacer(Modifier.height(4.dp))
            Description(text = messageFormatter(content), maxLines = 1)
            Spacer(Modifier.height(2.dp))
            LabelSmall(text = description)
        }
    }
}

@Composable
@Preview
fun PrescriptionCardPreview() {
    PrescriptionCard(
        title = "Amoxicillin 250mg",
        description = "Dr. Alexander Wolfe",
        content = "One to be taken three times a day after food",
        imageUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/blob/master/setup/users/portrait_smiling_handsome_male_doctor_man-abe6a9adfc322800a12e75590503ef09-6cd8a2.png",
        changeTime = "2:53pm",
    )
}

@Composable
fun Prescriptions(
    prescriptions: List<Prescription> = mockedPrescriptions,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PrescriptionList(prescriptions)
    }
}
