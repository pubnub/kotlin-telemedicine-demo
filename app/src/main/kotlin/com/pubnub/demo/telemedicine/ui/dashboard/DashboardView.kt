package com.pubnub.demo.telemedicine.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.pubnub.demo.telemedicine.data.channel.ChannelType
import com.pubnub.demo.telemedicine.result.TestResults
import com.pubnub.demo.telemedicine.ui.Background
import com.pubnub.demo.telemedicine.ui.casestudy.list.Cases
import com.pubnub.demo.telemedicine.ui.chat.Chats
import com.pubnub.demo.telemedicine.ui.common.FeatureNotImplemented
import com.pubnub.demo.telemedicine.ui.common.MyAppBar
import com.pubnub.demo.telemedicine.ui.contacts.Contacts
import com.pubnub.demo.telemedicine.ui.prescription.Prescriptions
import com.pubnub.demo.telemedicine.util.extension.Border
import com.pubnub.demo.telemedicine.util.extension.border
import com.pubnub.framework.PubNubFramework
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class, FlowPreview::class)
@Composable
fun DashboardView(
    userId: UserId,
    userName: String,
    profileUrl: String?,
    dashboardViewModel: DashboardViewModel = viewModel(),
) {
    val scope = rememberCoroutineScope()
    val state = rememberScaffoldState()
    val navController = rememberNavController()
    val isDoctor = dashboardViewModel.getUser(userId)!!.isDoctor()

    Background {

        Scaffold(
            scaffoldState = state,
            topBar = {
                MyAppBar(
                    title = { AppBarTitle(currentRoute(navController) ?: "Unknown") },
                    navigationIcon = {
                        AppBarIcon(icon = Icons.Outlined.Menu,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClick = { scope.launch { state.drawerState.open() } }))
                    }

                )
            },
            drawerBackgroundColor = MaterialTheme.colors.primary,
            drawerContent = {
                Column(Modifier.padding(8.dp)) {
                    AppBarIcon(icon = Icons.Outlined.Clear,
                        modifier = Modifier.clickable(onClick = { scope.launch { state.drawerState.close() } }))
                    Drawer(userId = userId, userName = userName, imageUrl = profileUrl, viewModel = viewModel())
                }
            },
            bottomBar = {
                BottomNavigation(navController, dashboardViewModel)
            },
            floatingActionButton = {
                val context = LocalContext.current
                when (currentRoute(navController)) {
                    NavigationScreens.Cases.route,
                    NavigationScreens.PatientChats.route,
                    NavigationScreens.DoctorChats.route,
                    -> {
                        FloatingActionButton(onClick = { FeatureNotImplemented.toast(context) }) {
                            Icon(Icons.Filled.Add, null)
                        }
                    }
                    NavigationScreens.Contacts.route -> {
                        FloatingActionButton(onClick = { FeatureNotImplemented.toast(context) }) {
                            Icon(Icons.Filled.PersonAdd, null)
                        }
                    }
                    else -> {
                    }
                }
            }
        ) {
            val modifier = Modifier
                .fillMaxSize()
                .padding(it)
            NavHost(
                navController = navController,
                startDestination = if(isDoctor) NavigationScreens.Cases.route else NavigationScreens.DoctorChats.route,
            ) {
                composable(NavigationScreens.Cases.route) {
                    Cases(
                        cases = dashboardViewModel.getCases(userId),
                        onClick = { dashboardViewModel.navigateToCase(it) },
                        modifier = modifier,
                    )
                }
                composable(NavigationScreens.PatientChats.route) {
                    Chats(
                        chats = dashboardViewModel.getPatientChats(userId),
                        onClick = { channelId ->
                            dashboardViewModel.navigateToConversation(channelId,
                                ChannelType.PATIENT)
                        },
                        modifier = modifier,
                    )
                }
                composable(NavigationScreens.DoctorChats.route) {
                    Chats(
                        chats = dashboardViewModel.getDoctorChats(userId),
                        onClick = { channelId ->
                            dashboardViewModel.navigateToConversation(channelId, ChannelType.DOCTOR)
                        },
                        modifier = modifier,
                    )
                }
                composable(NavigationScreens.Contacts.route) {
                    Contacts(
                        contacts = dashboardViewModel.getContacts(userId),
                        modifier = modifier,
                    )
                }
                composable(NavigationScreens.Prescription.route){
                    Prescriptions(modifier = modifier)
                }
                composable(NavigationScreens.TestResult.route){
                    TestResults(modifier = modifier)
                }
            }

        }
    }
}

@Composable
@Preview
private fun DashboardViewPreview() {
    DashboardView(
        userId = "0",
        userName = "Saleha Admad",
        profileUrl = "https://github.com/pubnub/kotlin-telemedicine-demo/raw/master/setup/users/cheerful_korean_business_lady_posing_office_with_crossed_arms-7e84991259ab033eb216f5c16ca89fcb-6ed401.png",
    )
}

@Composable
fun AppBarTitle(title: String) =
    Text(text = title, fontSize = 20.sp, modifier = Modifier
        .fillMaxWidth())

@Composable
fun AppBarIcon(icon: ImageVector, modifier: Modifier = Modifier) =
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier
            .size(40.dp)
            .padding(8.dp)
    )

@Composable
fun BottomNavigation(
    controller: NavHostController,
    dashboardViewModel: DashboardViewModel,
    currentUserId: UserId = PubNubFramework.userId,
) {
    val user = dashboardViewModel.getUser(currentUserId)!!

    val getCasesCount =
        if (user.isDoctor()) dashboardViewModel.getUnreadCaseMessagesCount(currentUserId) else 0L
    val getMessagesCount = dashboardViewModel.getUnreadChatMessagesCount(currentUserId)

    val notificationCount: (NavigationScreens) -> Long = { screen ->
        when (screen) {
            NavigationScreens.Cases -> getCasesCount
            NavigationScreens.PatientChats,
            NavigationScreens.DoctorChats,
            -> getMessagesCount
            NavigationScreens.Contacts -> 0L
            NavigationScreens.TestResult -> 0L
            NavigationScreens.Prescription -> 0L
        }
    }

    val navigationItems = if (user.isDoctor()) DOCTOR_SCREENS else PATIENT_SCREENS
    AppBottomNavigation(navController = controller, items = navigationItems, notificationCount = notificationCount)

}

private val DOCTOR_SCREENS = listOf(
    NavigationScreens.Cases,
    NavigationScreens.PatientChats,
    NavigationScreens.Contacts,
)
private val PATIENT_SCREENS = listOf(
    NavigationScreens.DoctorChats,
    NavigationScreens.TestResult,
    NavigationScreens.Prescription,
)

@Composable
private fun AppBottomNavigation(
    navController: NavHostController,
    items: List<NavigationScreens>,
    notificationCount: (NavigationScreens) -> Long = { 0L },
) {
    MyBottomNavigation {
        val currentRoute = currentRoute(navController)

        items.forEach { screen ->
            val selected = currentRoute == screen.route
            val border =
                if (selected) Modifier.border(bottom = Border(2.dp, Color.White)) else Modifier

            Box(modifier = Modifier.weight(1f, fill = true)) {
                Row(modifier = Modifier.fillMaxSize()) {
                    BottomNavigationItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo = navController.graph.startDestination
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = screen.iconRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(35.dp)
                                    .padding(4.dp, 4.dp, 4.dp, 8.dp),
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = screen.resourceId).toUpperCase(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        alwaysShowLabel = true,
                        modifier = border
                            .fillMaxSize()
                            .padding(0.dp, 6.dp)
                    )
                }
                NotificationBadge(count = notificationCount(screen),
                    backgroundAlpha = 1f,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .absoluteOffset(26.dp, 8.dp) // icon size is 24x24 + 2px offset
                )
            }
        }
    }
}

@Composable
internal fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.arguments?.getString(KEY_ROUTE)
}

// region Summary
@Composable
fun Label(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.87f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
fun LabelSmall(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.87f),
        modifier = modifier
    )
}

@Composable
fun Description(text: AnnotatedString, maxLines: Int = 2, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.65f),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun DateLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.54f),
        modifier = modifier
    )
}

@Composable
fun NotificationBadge(count: Long, backgroundAlpha: Float = 0.74f, modifier: Modifier = Modifier) {
    if (count <= 0L) return
    val text = if (count <= 99) "$count" else "+"
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colors.background,
        textAlign = TextAlign.Center,
        modifier = modifier
            .size(22.dp)
            .background(MaterialTheme.colors.secondary.copy(alpha = backgroundAlpha), CircleShape)
            .padding(0.dp, 2.dp)
    )
}
//
