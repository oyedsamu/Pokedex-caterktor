package com.mocoding.pokedex.ui.feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
internal fun LaunchedLoadMore(onLoadMore: suspend () -> Unit) {
    LaunchedEffect(Unit) {
        onLoadMore()
    }
}

internal fun String.prettyName(): String =
    replace('-', ' ').replaceFirstChar { it.uppercaseChar() }
