package com.mocoding.pokedex.ui.evolutions

import androidx.compose.foundation.layout.*
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
import com.mocoding.pokedex.ui.feature.prettyName
import com.mocoding.pokedex.ui.helper.LocalSafeArea
import com.mocoding.pokedex.ui.pokedex.components.PokemonGrid
import com.mocoding.pokedex.ui.evolutions.store.EvolutionsStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EvolutionsScreen(component: EvolutionsComponent) {
    val state by component.state.collectAsState()
    val visiblePokemon = state.pokemonList.filter {
        state.searchValue.isBlank() || it.name.contains(state.searchValue, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evolutions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { component.onOutput(EvolutionsComponent.Output.NavigateBack) }) {
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
                onValueChange = { component.onEvent(EvolutionsStore.Intent.UpdateSearchValue(it)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                label = { Text("Search Pokemon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            )
            Text(
                text = "Select a Pokemon to inspect its evolution chain.",
                modifier = Modifier.padding(horizontal = 20.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                )
            }
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(20.dp))
            }
            PokemonGrid(
                onPokemonClicked = { component.onOutput(EvolutionsComponent.Output.NavigateToEvolutionDetails(it)) },
                pokemonList = visiblePokemon,
                isLoading = !state.isLastPageLoaded,
                loadMoreItems = {
                    if (state.pokemonList.isEmpty()) return@PokemonGrid
                    val nextPage = state.pokemonList.last().page + 1
                    component.onEvent(EvolutionsStore.Intent.LoadPokemonListByPage(nextPage))
                },
            )
        }
    }
}
