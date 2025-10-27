package com.example.bghelp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp

private val mainPadding = PaddingValues(
    top = 16.dp,
    bottom = 0.dp,
    start = 16.dp,
    end = 16.dp
)

private val largePadding = PaddingValues(
    start = 16.dp,
    end = 16.dp,
    top = 12.dp,
    bottom = 12.dp
)

private val smallPadding = PaddingValues(
    start = 16.dp,
    end = 16.dp,
    top = 4.dp,
    bottom = 4.dp
)

@Composable
fun MainContentContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(mainPadding),
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
    padding: PaddingValues = largePadding,
    backgroundColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .let { if (backgroundColor != null) it.background(backgroundColor) else it }
            .padding(padding)
            .fillMaxWidth(),
        content = content
    )
}

@Composable
fun HighlightedContainerSmall(
    modifier: Modifier = Modifier,
    padding: PaddingValues = smallPadding,
    backgroundColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .let { if (backgroundColor != null) it.background(backgroundColor) else it }
            .padding(padding)
            .fillMaxWidth(),
        content = content
    )
}