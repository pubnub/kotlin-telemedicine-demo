package com.pubnub.demo.telemedicine.ui.patient

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.ui.Background
import com.pubnub.demo.telemedicine.ui.common.AppBar
import com.pubnub.demo.telemedicine.ui.common.FeatureNotImplemented
import com.pubnub.demo.telemedicine.ui.common.HEADER_HEIGHT
import com.pubnub.demo.telemedicine.ui.common.UserInfoDetails
import com.pubnub.framework.data.ChannelId

@Composable
fun PatientInfoView(caseId: ChannelId) {

    val activity = LocalContext.current as Activity
    val backAction = { activity.finish() }

    val viewModel: PatientInfoViewModel = viewModel(key = caseId)
    val info = viewModel.getInfo(caseId)!!

    PatientInfo(
        name = info.name,
        description = info.description,
        imageUrl = info.imageUrl,
        backAction = backAction,
    )
}

@Composable
private fun PatientInfo(
    name: String,
    description: String,
    imageUrl: String,
    backAction: () -> Unit = {},
) {
    Background {
        Scaffold(
            topBar = {
                AppBar(height = HEADER_HEIGHT) {
                    PatientInfoHeader(
                        name = name,
                        description = description,
                        imageUrl = imageUrl,
                        backAction = backAction,
                    )
                }
            },
        ) {
            PatientMenu(Modifier.fillMaxSize())
        }
    }
}

@Composable
fun PatientInfoHeader(
    name: String,
    description: String,
    imageUrl: String,
    backAction: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .clickable(onClick = { backAction.invoke() })
                .padding(12.dp),
        )

        UserInfoDetails(
            name = name,
            description = description,
            imageUrl = imageUrl,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
@Preview
private fun PatientInfoHeaderPreview() {
    PatientInfoHeader(
        name = "Saleha Ahmad",
        description = "47 yo · Pyrexia of unknown origin",
        imageUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png",
        backAction = {},
    )
}

@Composable
fun PatientMenu(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {

        val menuItems = arrayOf(
            MenuItem(
                icon = ImageVector.vectorResource(id = R.drawable.ic_address_card),
                title = "Patient Profile",
                padding = PaddingValues(2.dp, 6.dp),
            ) {
                FeatureNotImplemented.toast(context)
            },
            MenuItem(
                icon = Icons.Filled.LibraryBooks,
                title = "Case Summary"
            ) {
                FeatureNotImplemented.toast(context)
            },
            MenuItem(
                icon = Icons.Filled.Attachment,
                title = "Images"
            ) {
                FeatureNotImplemented.toast(context)
            },
            MenuItem(
                icon = ImageVector.vectorResource(id = R.drawable.ic_prescription),
                title = "Prescriptions",
                padding = PaddingValues(5.dp),
            ) {
                FeatureNotImplemented.toast(context)
            },
            MenuItem(
                icon = ImageVector.vectorResource(id = R.drawable.ic_participants),
                title = "Participants",
                extra = "5"
            ) {
                FeatureNotImplemented.toast(context)
            },
        )
        menuItems.forEachIndexed { index, (icon, title, extra, padding, action) ->
            Item(icon = icon, title = title, extra = extra, padding = padding, action = action)
            if (index < menuItems.size - 1)
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
                )
        }
    }
}

private data class MenuItem(
    val icon: ImageVector,
    val title: String,
    val extra: String? = null,
    val padding: PaddingValues = PaddingValues(0.dp),
    val action: () -> Unit = {},
)

@Composable
private fun Item(
    icon: ImageVector,
    title: String,
    action: () -> Unit,
    padding: PaddingValues,
    extra: String? = null,
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = action)
            .padding(20.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(35.dp).padding(padding),
            tint = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.width(32.dp))
        Text(text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f))

        if (extra != null)
            Text(text = extra,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF8a8a8a))

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
@Preview
private fun DrawerPreview() {
    Background {
        PatientInfo(
            name = "Saleha Admad",
            description = "47 yo · Pyrexia of unknown origin",
            imageUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png"
        )
    }
}
