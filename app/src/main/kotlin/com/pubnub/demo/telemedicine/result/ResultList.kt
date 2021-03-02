package com.pubnub.demo.telemedicine.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pubnub.demo.telemedicine.ui.conversation.messageFormatter
import com.pubnub.demo.telemedicine.ui.dashboard.DateLabel
import com.pubnub.demo.telemedicine.ui.dashboard.Description
import com.pubnub.demo.telemedicine.ui.dashboard.Label

private val DividerModifier = Modifier.padding(95.dp, 0.dp, 20.dp, 0.dp)
private val CardModifier = Modifier.padding(20.dp)

private val mockedResults = listOf(
    TestResult(
        title = "SAARS-COV2",
        description = "Positive",
        date = "8:12am",
    ),
    TestResult(
        title = "HEMOGLOBIN A1C",
        description = "Normal 5.4%",
        date = "YESTERDAY",
    ),
    TestResult(
        title = "VITAMIN D",
        description = "Insufficient",
        date = "2/8/21",
    ),
    TestResult(
        title = "FASTING GLUCOSE",
        date = "3/6/20",
    ),
    TestResult(
        title = "FASTING LIPID PANEL",
        date = "5/6/19",
    ),
)

@Composable
fun TestResultList(chats: List<TestResult>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(chats) { index, item ->
            TestResultCard(
                title = item.title,
                description = item.description,
                date = item.date,
                modifier = Modifier.then(CardModifier)
            )
            if (index < chats.size - 1)
                Divider(modifier = DividerModifier)
        }
    }
}

@Composable
fun TestResultCard(
    title: String,
    description: String? = null,
    date: String = "09:47 AM",
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.Receipt,
            contentDescription = null,
            modifier = Modifier.width(50.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Row {
                Label(text = title, modifier = Modifier.weight(1f))
                DateLabel(text = date)
            }
            description?.let {
                Spacer(Modifier.height(4.dp))
                Description(text = messageFormatter(description), maxLines = 1)
            }
        }
    }
}

@Composable
@Preview
fun TestResultCardPreview() {
    TestResultCard(
        title = "SAARS-COV2",
        description = "Positive",
        date = "8:12am",
    )
}

@Composable
@Preview
fun TestResultCardWithoutDescriptionPreview() {
    TestResultCard(
        title = "SAARS-COV2",
        date = "8:12am",
    )
}

@Composable
fun TestResults(
    prescriptions: List<TestResult> = mockedResults,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        TestResultList(prescriptions)
    }
}

data class TestResult(
    val title: String,
    val description: String? = null,
    val date: String,
)
