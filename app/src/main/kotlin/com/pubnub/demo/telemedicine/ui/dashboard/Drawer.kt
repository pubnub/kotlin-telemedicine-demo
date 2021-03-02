package com.pubnub.demo.telemedicine.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.pubnub.demo.telemedicine.ui.Background
import com.pubnub.demo.telemedicine.util.CenterInside
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun Drawer(
    prefix: String = "Hello",
    userId: String,
    userName: String,
    imageUrl: String? = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png",
    viewModel: DrawerViewModel? = null
) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(30.dp)) {
                Spacer(modifier = Modifier.height(48.dp))
                UserDetails(prefix = prefix,
                    userName = userName,
                    imageUrl = imageUrl,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.onSecondary.copy(alpha = 0.45f),
                )

            }
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {

                val menuItems = arrayOf(
                    MenuItem(Icons.Filled.Person, "My Account") {
                        viewModel?.navigateToMyAccount(userId)
                    },
                    MenuItem(Icons.Filled.HelpOutline, "Help Center") {
                        viewModel?.navigateToHelpCenter()
                    },
                    MenuItem(Icons.Filled.LibraryBooks, "Terms & Conditions") {
                        viewModel?.navigateToTerms()
                    },
                    MenuItem(Icons.Filled.ExitToApp, "Logout") {
                        viewModel?.logout()
                    }
                )
                menuItems.forEach { (icon, title, action) ->
                    DrawerItem(icon = icon, title = title, action = action, modifier = Modifier.padding(30.dp, 14.dp))
                }
            }
    }
}

private data class MenuItem(
    val icon: ImageVector,
    val title: String,
    val action: () -> Unit = {},
)

@Composable
fun UserDetails(
    prefix: String,
    userName: String,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        CoilImage(
            request = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = null,
            contentScale = CenterInside,
            modifier = Modifier.size(54.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = prefix, fontSize = 15.sp)
            Text(text = userName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    title: String,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = action)
            .then(modifier),
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(35.dp))

        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
@Preview
private fun DrawerPreview() {
    Background {
        Drawer(userId = "0", userName = "Saleha Admad", viewModel = null)
    }
}

@Composable
@Preview(
    device = Devices.NEXUS_5
)
private fun DrawerPreviewNexus() {
    DrawerPreview()
}

@Composable
@Preview(
    widthDp = 320,
    heightDp = 640
)
private fun DrawerPreviewUXSize() {
    DrawerPreview()
}
