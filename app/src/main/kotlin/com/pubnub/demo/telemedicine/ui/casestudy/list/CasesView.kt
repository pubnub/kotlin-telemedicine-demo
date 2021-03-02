package com.pubnub.demo.telemedicine.ui.casestudy.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.ui.Root
import com.pubnub.demo.telemedicine.ui.casestudy.Case
import com.pubnub.demo.telemedicine.ui.conversation.messageFormatter
import com.pubnub.demo.telemedicine.ui.dashboard.DateLabel
import com.pubnub.demo.telemedicine.ui.dashboard.Description
import com.pubnub.demo.telemedicine.ui.dashboard.Label
import com.pubnub.demo.telemedicine.ui.dashboard.LabelSmall
import com.pubnub.demo.telemedicine.ui.dashboard.NotificationBadge
import com.pubnub.demo.telemedicine.ui.dashboard.WelcomeHeader
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

@Composable
fun CasesView(userId: UserId) {
    Root {
        val viewModel: CaseListViewModel = viewModel()

        WelcomeHeader(title = viewModel.getUsername(userId))
        Cases(cases = viewModel.getCases(userId), onClick = {})
    }
}

@Composable
fun Cases(
    cases: List<Case>,
    onClick: (ChannelId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showActiveCases: MutableState<Boolean> = remember { mutableStateOf(true) }

    Column(modifier = modifier) {
        Tabs(showActiveCases)
        CaseList(cases, showActiveCases.value, onClick)
    }
}

@Composable
private fun Tabs(
    showActiveCases: MutableState<Boolean> = remember { mutableStateOf(true) },
) {
    val titles = listOf(
        "Active" to true,
        "Closed" to false
    )
    TabRow(
        selectedTabIndex = if (showActiveCases.value) 0 else 1,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.primary,
        divider = {
            TabRowDefaults.Divider(color = MaterialTheme.colors.onSurface.copy(alpha = TabRowDefaults.DividerOpacity))
        }
    ) {
        titles.forEach { (title, isActive) ->
            val selected = showActiveCases.value == isActive
            val textColor =
                if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(
                    alpha = 0.6f)
            Tab(
                text = {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = textColor,
                    )
                },
                selected = selected,
                onClick = { showActiveCases.value = isActive },
                modifier = Modifier.height(48.dp),
            )
        }
    }
}

@Composable
private fun CaseList(cases: List<Case>, active: Boolean, onClick: (ChannelId) -> Unit) {
    val currentCases = cases.filter { case ->
        case.closed == !active
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(currentCases) { index, case ->
            val ClickableModifier =
                if (active) Modifier.clickable(onClick = { onClick(case.id) }) else Modifier
            CaseCard(
                active = active,
                title = case.title,
                description = case.description,
                content = messageFormatter(text = case.lastMessage?.content ?: ""),
                notifications = case.notifications,
                changeTime = case.lastMessage?.dateString ?: "",
                modifier = ClickableModifier.then(CaseModifier),
            )
            if (index < cases.size - 1)
                Divider()
        }
    }
}

private val CaseModifier = Modifier.padding(20.dp)

@Composable
fun CaseCard(
    active: Boolean,
    title: String,
    description: String,
    content: AnnotatedString,
    changeTime: String = "09:47 AM",
    closeDate: String = "12/21/20",
    notifications: Long = 0L,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row {
            Label(text = title, modifier = Modifier.weight(1f))
            if (active) {
                DateLabel(text = changeTime)
            }
        }
        Spacer(Modifier.height(4.dp))
        LabelSmall(text = description)
        Spacer(Modifier.height(2.dp))
        if (active) {
            if (content.text.isNotEmpty())
                Row(verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.heightIn(min = 22.dp)) {
                    Description(text = content, modifier = Modifier.weight(1f))
                    NotificationBadge(notifications)
                }
        } else {
            Description(text = messageFormatter(text = stringResource(id =
            R.string.case_closed_on, closeDate)))
        }
    }
}

@Composable
@Preview
private fun CaseCardActivePreview() {
    CaseCard(
        active = true,
        title = "Pyrexia of unknown origin",
        description = "Saleha Ahmad • 47 yo",
        content = messageFormatter(text = "*Dr Zaidi* Patient is not responding to treatment."),
        changeTime = "09:47 AM",
        notifications = 5,
    )
}

@Composable
@Preview
private fun CaseCardClosedPreview() {
    CaseCard(
        active = false,
        title = "Pyrexia of unknown origin",
        description = "Saleha Ahmad • 47 yo",
        content = messageFormatter(text = "*Dr Zaidi* Patient is not responding to treatment."),
        closeDate = "12/21/20",
        notifications = 5,
    )
}
