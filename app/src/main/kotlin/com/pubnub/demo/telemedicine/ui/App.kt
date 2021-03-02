package com.pubnub.demo.telemedicine.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import com.pubnub.demo.telemedicine.ui.theme.AppTheme

@Composable
fun Root(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    drawer: @Composable() ((ScaffoldState) -> Unit)? = null,
    content: @Composable() (ScaffoldState) -> Unit,
) {
    AppTheme {
        BoxWithConstraints {
            val drawerContent: @Composable() (ColumnScope.() -> Unit)? =
                if (drawer == null) null
                else {
                    { drawer.invoke(scaffoldState) }
                }

            Scaffold(
                scaffoldState = scaffoldState,
                drawerContent = drawerContent,
                content = {
                    content.invoke(scaffoldState)
                },
            )
        }
    }
}

@Composable
fun Background(
    content: @Composable() (BoxWithConstraintsScope) -> Unit,
) {
    AppTheme {
        BoxWithConstraints {
            content.invoke(this@BoxWithConstraints)
        }
    }
}

