package com.mocoding.pokedex.ui.evolutions

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
import com.mocoding.pokedex.core.model.EvolutionInfo
import com.mocoding.pokedex.ui.feature.prettyName
import com.mocoding.pokedex.ui.helper.LocalSafeArea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EvolutionDetailsScreen(component: EvolutionDetailsComponent) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.evolutionInfo?.pokemonName?.prettyName() ?: "Evolution", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { component.onOutput(EvolutionDetailsComponent.Output.NavigateBack) }) {
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
                state.evolutionInfo != null -> {
                    val info = state.evolutionInfo ?: return@Box
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item("summary") {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Root species: ${info.rootName.prettyName()}")
                                info.previousEvolutionName?.let {
                                    Text("Evolves from: ${it.prettyName()}")
                                }
                            }
                        }
                        items(info.nodes, key = { "${it.stage}-${it.pokemonName}" }) { node ->
                            EvolutionNodeCard(node)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EvolutionNodeCard(node: EvolutionInfo.EvolutionNode) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Text(node.pokemonName.prettyName(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Stage ${node.stage + 1}${if (node.isBaby) " • baby" else ""}")
            if (node.conditions.isNotEmpty()) {
                Text("Evolution conditions", style = MaterialTheme.typography.labelLarge)
                node.conditions.forEach { Text(it.description.ifBlank { "No special condition" }) }
            }
            if (node.evolvesTo.isNotEmpty()) {
                Text("Evolves to: ${node.evolvesTo.joinToString { it.prettyName() }}")
            }
        }
    }
}
