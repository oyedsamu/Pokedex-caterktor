package com.mocoding.pokedex.ui.locations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mocoding.pokedex.ui.feature.prettyName
import com.mocoding.pokedex.ui.helper.LocalSafeArea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocationDetailsScreen(component: LocationDetailsComponent) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.stateTitle(), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { component.onOutput(LocationDetailsComponent.Output.NavigateBack) }) {
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
                state.locationAreaInfo != null -> {
                    val info = state.locationAreaInfo ?: return@Box
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item("meta") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                info.locationName?.let { Text("Location: ${it.prettyName()}") }
                                info.regionName?.let { Text("Region: ${it.prettyName()}") }
                                Text("Game index: ${info.gameIndex}")
                                if (info.areaNames.isNotEmpty()) {
                                    Text("Areas: ${info.areaNames.joinToString { it.prettyName() }}")
                                }
                            }
                        }
                        item("methods") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Encounter methods", style = MaterialTheme.typography.titleMedium)
                                info.encounterMethods.forEach { method ->
                                    Text("${method.name.prettyName()}: ${method.versionRates.joinToString { "${it.versionName.prettyName()} ${it.rate}%" }}")
                                }
                            }
                        }
                        if (info.pokemonEncounters.isEmpty()) {
                            item("empty") {
                                Text("No encounter data is available for this location area.")
                            }
                        } else {
                            item("encounters-title") {
                                Text("Pokemon encounters", style = MaterialTheme.typography.titleMedium)
                            }
                            items(info.pokemonEncounters, key = { it.pokemonName }) { encounter ->
                                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(16.dp),
                                    ) {
                                        Text(encounter.pokemonName.prettyName(), style = MaterialTheme.typography.titleMedium)
                                        encounter.versions.forEach { version ->
                                            Text(
                                                "${version.versionName.prettyName()} • max ${version.maxChance}%",
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                            version.encounters.forEach { detail ->
                                                Text(
                                                    "${detail.methodName.prettyName()} • L${detail.minLevel}-${detail.maxLevel} • ${detail.chance}%${
                                                        if (detail.conditionValues.isNotEmpty()) " • ${detail.conditionValues.joinToString { it.prettyName() }}" else ""
                                                    }",
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun com.mocoding.pokedex.ui.locations.store.LocationDetailsStore.State.stateTitle(): String =
    locationAreaInfo?.name?.prettyName() ?: "Location"
