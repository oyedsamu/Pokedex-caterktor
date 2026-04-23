package com.mocoding.pokedex.ui.watch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mocoding.pokedex.ui.helper.LocalSafeArea
import com.mocoding.pokedex.ui.main.components.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WatchDetailsScreen(component: WatchDetailsComponent) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.video?.title ?: "Watch", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { component.onOutput(WatchDetailsComponent.Output.NavigateBack) }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        modifier = Modifier.padding(LocalSafeArea.current),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> Text(state.error ?: "")
                state.video != null -> {
                    val video = state.video ?: return@Box
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        AsyncImage(
                            url = video.imageUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.7f),
                        )
                        Text(video.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("${video.year} • ${video.category} • ${video.details}")
                        Text(video.synopsis, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
