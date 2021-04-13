package com.pubnub.demo.telemedicine.ui.contacts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.pubnub.demo.telemedicine.R
import com.pubnub.demo.telemedicine.util.CenterInside
import com.google.accompanist.coil.CoilImage

@Composable
fun Contacts(
    contacts: List<Contact>,
    modifier: Modifier = Modifier,
) {
    val showDoctors: MutableState<Boolean> = remember { mutableStateOf(true) }
    val physiciansLazyListState =
        rememberLazyListState(initialFirstVisibleItemIndex = contacts.filter { it.isDoctor }.size)
    val patientsLazyListState =
        rememberLazyListState(initialFirstVisibleItemIndex = contacts.filter { !it.isDoctor }.size)

    Column(modifier = modifier) {
        Tabs(showDoctors)
        ContactList(
            contacts = contacts.filter { it.isDoctor == showDoctors.value },
            lazyListState = if (showDoctors.value) physiciansLazyListState else patientsLazyListState
        )
    }
}

@Composable
private fun Tabs(
    showActiveCases: MutableState<Boolean> = remember { mutableStateOf(true) },
) {
    val titles = listOf(
        "Physicians" to true,
        "Patients" to false
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
fun ContactList(
    contacts: List<Contact>,
    lazyListState: LazyListState,
) {

    LazyColumn(modifier = Modifier.fillMaxSize(), state = lazyListState) {
        itemsIndexed(contacts) { index, contact ->

            val prevContact = contacts.getOrNull(index - 1)

            // Draw Letter separator
            val letter = contact.name.first()
            if (prevContact?.name?.startsWith(letter) != true) {
                LetterLabel(text = letter.toString(), modifier = LetterModifier)
                Divider()
            }
            ContactCard(
                title = contact.name,
                prefix = if (contact.isDoctor) stringResource(id = R.string.physician_prefix) else null,
                imageUrl = contact.imageUrl,
                modifier = Modifier.then(ContactModifier)
            )
            if (index < contacts.size - 1)
                Divider()
        }
    }
}

private val ContactModifier = Modifier.padding(16.dp, 12.dp)
private val LetterModifier = Modifier.padding(24.dp, 12.dp, 24.dp, 6.dp)

@Composable
fun ContactCard(
    title: String,
    imageUrl: String,
    prefix: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        CoilImage(
            request = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = null,
            contentScale = CenterInside,
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        // title name bold 16px center vert
        val textList = title.split(" ")
        val name = arrayOf(prefix, textList.first()).filterNotNull().joinToString(" ")
        val rest = textList.drop(1).joinToString(separator = " ", prefix = " ")

        val formattedTitle = AnnotatedString(
            text = name,
            spanStyle = SpanStyle(fontWeight = FontWeight.Bold)
        ).plus(
            AnnotatedString(
                text = rest,
                spanStyle = SpanStyle(fontWeight = FontWeight.Normal)
            )
        )
        Label(text = formattedTitle)
    }
}

@Composable
fun Label(text: AnnotatedString, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.87f),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
private fun LetterLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        color = MaterialTheme.colors.secondary,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
@Preview
private fun ContactCardPreview() {
    ContactCard(title = "Akio Chen", imageUrl = "")
}