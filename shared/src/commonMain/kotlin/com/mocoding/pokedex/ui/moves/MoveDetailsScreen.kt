package com.mocoding.pokedex.ui.moves

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mocoding.pokedex.ui.feature.prettyName
import com.mocoding.pokedex.ui.helper.LocalSafeArea
import com.mocoding.pokedex.ui.moves.store.MoveDetailsStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoveDetailsScreen(component: MoveDetailsComponent) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.moveInfo?.name?.prettyName() ?: "Move", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { component.onOutput(MoveDetailsComponent.Output.NavigateBack) }) {
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
                state.error != null -> Text(text = state.error ?: "")
                state.moveInfo != null -> MoveDetailsContent(state = state)
            }
        }
    }
}

@Composable
private fun MoveDetailsContent(state: MoveDetailsStore.State) {
    val move = state.moveInfo ?: return
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        AssistChip(onClick = {}, label = { Text(move.type.prettyName()) })
        InfoLine("Damage class", move.damageClass.prettyName())
        InfoLine("Target", move.target.prettyName())
        InfoLine("Power", move.power?.toString() ?: "Unknown")
        InfoLine("Accuracy", move.accuracy?.toString() ?: "Unknown")
        InfoLine("PP", move.pp?.toString() ?: "Unknown")
        InfoLine("Priority", move.priority.toString())
        InfoLine("Effect chance", move.effectChance?.toString() ?: "None")
        move.effect?.let { Text(it) }
        move.flavorText?.let {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(it, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
