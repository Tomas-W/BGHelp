package com.example.bghelp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MainContentContainer(
    modifier: Modifier = Modifier,
    spacing: Int = 0,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        verticalArrangement = if (spacing > 0) Arrangement.spacedBy(spacing.dp) else Arrangement.spacedBy(0.dp),
        content = content
    )
}

@Composable
fun LazyColumnContainer(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = state,
        content = content
    )
}

@Composable
fun SchedulableContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .let { if (backgroundColor != null) it.background(backgroundColor) else it }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        content = content
    )
}

@Composable
fun HighlightedContainerSmall(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .let { if (backgroundColor != null) it.background(backgroundColor) else it }
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        content = content
    )
}