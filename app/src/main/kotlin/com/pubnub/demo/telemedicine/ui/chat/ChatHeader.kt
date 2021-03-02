package com.pubnub.demo.telemedicine.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatHeader(
    active: Boolean,
    title: String,
    description: String,
    imageUrl: String? = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png",
    modifier: Modifier = Modifier,
    backAction: (() -> Unit),
    profileAction: (() -> Unit)? = null,
    callAction: (() -> Unit)? = null,
) {
    val backActionModifier = Modifier.clickable(onClick = { backAction() })
    val profileActionModifier = profileAction?.let {
        Modifier.clickable(onClick = { profileAction() })
    } ?: Modifier
    val callActionModifier = callAction?.let {
        Modifier.clickable(onClick = { callAction() })
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.then(profileActionModifier)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .then(backActionModifier)
                .padding(16.dp)
        )
        UserProfileImage(
            modifier = Modifier.size(40.dp),
            imageUrl = imageUrl,
            isOnline = active,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier
            .weight(1f)) {
            Label(text = title)
            //Spacer(Modifier.height(4.dp))
            LabelSmall(text = description)
        }
        callActionModifier?.let { modifier ->
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .then(modifier)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun CaseHeader(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    backAction: (() -> Unit)? = null,
    addAction: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .clickable(onClick = { backAction?.invoke() })
                .padding(16.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Label(text = title)
            //Spacer(Modifier.height(4.dp))
            LabelSmall(text = description)
        }
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .clickable(onClick = { addAction?.invoke() })
                .padding(16.dp),
        )
    }
}

@Composable
private fun Label(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = MaterialTheme.colors.onSecondary,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
private fun LabelSmall(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = MaterialTheme.colors.onSecondary,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
@Preview
private fun ChatHeaderDoctorPreview() {
    ChatHeader(
        active = false,
        title = "Dr. Sam Smith",
        description = "Radiologist · Washington Hospital",
        imageUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/asian_young_main_group_hospital_professional-c0ba747cc87f47e9e774a98d96ab200e-9941ad.png",
        backAction = {},
        callAction = {},
    )
}

@Composable
@Preview
private fun ChatHeaderPatientPreview() {
    ChatHeader(
        active = true,
        title = "Saleha Ahmad",
        description = "47 yo",
        imageUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/asian_young_main_group_hospital_professional-c0ba747cc87f47e9e774a98d96ab200e-9941ad.png",
        backAction = {},
    )
}

@Composable
@Preview
private fun CaseHeaderPreview() {
    CaseHeader(
        title = "Pyrexia",
        description = "Saleha Admad · 47 yo",
    )
}
