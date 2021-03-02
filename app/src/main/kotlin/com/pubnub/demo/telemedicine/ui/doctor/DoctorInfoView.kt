package com.pubnub.demo.telemedicine.ui.doctor

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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

@Composable
fun DoctorInfoView(userId: String) {

    val activity = LocalContext.current as Activity
    val backAction = { activity.finish() }

    val viewModel: DoctorInfoViewModel = viewModel(key = userId)
    val info = viewModel.getInfo(userId)!!

    DoctorInfo(
        name = stringResource(R.string.physician_name, info.name),
        description = info.description,
        imageUrl = info.imageUrl,
        backAction = backAction,
    )
}

@Composable
private fun DoctorInfo(
    name: String,
    description: String,
    imageUrl: String,
    backAction: () -> Unit = {},
){
    Background {
        Scaffold(
            topBar = {
                AppBar(height = HEADER_HEIGHT) {
                    DoctorInfoHeader(
                        name = name,
                        description = description,
                        imageUrl = imageUrl,
                        backAction = backAction,
                    )
                }
            },
        ) {
            DoctorMenu(Modifier.fillMaxSize())
        }
    }
}

@Composable
fun DoctorInfoHeader(
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
                .padding(12.dp)
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
private fun DoctorInfoHeaderPreview() {
    DoctorInfoHeader(
        name = "Dr. Sam Smith",
        description = "Radiologist · Washington Hospital",
        imageUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/blob/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png",
        backAction = {},
    )
}

@Composable
fun DoctorMenu(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {

        val menuItems = arrayOf(
            MenuItem(Icons.Filled.Place, "Office Address", "1600 Owens St.\nSan Francisco, CA 9458") {
                FeatureNotImplemented.toast(context)
            },
            MenuItem(Icons.Filled.LocalHospital, "Plans Accepted", "Anthem, Kaiser Permanente,\nBlue Shield of California,\nMolina Healthcare") {
                FeatureNotImplemented.toast(context)
            },
            MenuItem(Icons.Filled.AccessTime, "Office Hours", "M-F 9:00am - 5:00pm") {
                FeatureNotImplemented.toast(context)
            },
        )
        menuItems.forEachIndexed { index, (icon, title, description, action) ->
            Item(icon = icon, title = title, description = description, action = action)
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
    val description: String,
    val action: () -> Unit = {},
)

@Composable
private fun Item(
    icon: ImageVector,
    title: String,
    description: String,
    action: () -> Unit,
) {
    Row(verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = action)
            .padding(20.dp)
    ) {

        Icon(imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(35.dp),
            tint = MaterialTheme.colors.primary)

        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier
            .weight(1f)
            .offset(y = 5.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = description,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .offset(y = 5.dp),
            tint = MaterialTheme.colors.primary
        )
    }
}

@Composable
@Preview
private fun DrawerPreview() {
    Background {
        DoctorInfo(
            name = "Dr. Sam Smith",
            description = "Radiologist · Washington Hospital",
            imageUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/blob/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png",
        )
    }
}
