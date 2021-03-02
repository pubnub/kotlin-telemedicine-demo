package com.pubnub.framework.ui.icon

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.pubnub.framework.ui.R


val Icons.Filled.Visibility: ImageVector
@Composable
get() = ImageVector.vectorResource(id = R.drawable.ic_baseline_visibility_24)

val Icons.Filled.VisibilityOff: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_baseline_visibility_off_24)
