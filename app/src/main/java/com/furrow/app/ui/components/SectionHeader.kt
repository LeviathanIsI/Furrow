package com.furrow.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    AppSectionHeader(
        title = title,
        modifier = modifier,
    )
}
