package com.mocoding.pokedex.ui.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mocoding.pokedex.core.model.LocationAreaSummary
import com.mocoding.pokedex.ui.feature.LaunchedLoadMore
import com.mocoding.pokedex.ui.feature.prettyName
import com.mocoding.pokedex.ui.helper.LocalSafeArea
import com.mocoding.pokedex.ui.locations.store.LocationsStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocationsScreen(component: LocationsComponent) {
    val state by component.state.collectAsState()
    val visibleLocations = state.locations.filter {
        state.searchValue.isBlank() || it.name.contains(state.searchValue, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locations", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { component.onOutput(LocationsComponent.Output.NavigateBack) }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        modifier = Modifier.padding(LocalSafeArea.current),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = state.searchValue,
                onValueChange = { component.onEvent(LocationsStore.Intent.UpdateSearchValue(it)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                label = { Text("Search locations") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            )
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 20.dp))
            }
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(visibleLocations, key = { it.name }) { location ->
                    LocationRow(location) {
                        component.onOutput(LocationsComponent.Output.NavigateToLocationDetails(location.name))
                    }
                }
                item("load-more") {
                    if (!state.isLastPageLoaded && state.locations.isNotEmpty()) {
                        LaunchedLoadMore {
                            val nextPage = state.locations.size / 20L
                            component.onEvent(LocationsStore.Intent.LoadLocationsByPage(nextPage))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationRow(location: LocationAreaSummary, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Text(location.name.prettyName(), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
    }
}
