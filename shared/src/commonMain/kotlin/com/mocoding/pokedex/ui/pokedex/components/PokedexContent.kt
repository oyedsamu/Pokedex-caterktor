package com.mocoding.pokedex.ui.pokedex.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mocoding.pokedex.ui.helper.LocalSafeArea
import com.mocoding.pokedex.ui.pokedex.PokedexComponent
import com.mocoding.pokedex.ui.pokedex.store.PokedexStore
import com.mocoding.pokedex.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PokedexContent(
    state: PokedexStore.State,
    onEvent: (PokedexStore.Intent) -> Unit,
    onOutput: (PokedexComponent.Output) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onOutput(PokedexComponent.Output.NavigateBack)
                        },
                    ) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = Modifier.padding(LocalSafeArea.current)
    ) {  paddingValue ->
        Box(
            modifier = Modifier.padding(paddingValue)
        ) {

            state.error?.let { error ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = error)
                }
            }

            Column {
                Text(
                    text = "Pokedex",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp, bottom = 6.dp)
                )

                val containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .2f)
                TextField(
                    value = state.searchValue,
                    onValueChange = { onEvent(PokedexStore.Intent.UpdateSearchValue(it)) },
                    placeholder = { Text(text = "Search Pokemon") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, contentDescription = "Search Pokemon")
                    },
                    trailingIcon = {
                        if (state.searchValue.isNotEmpty()) {
                            IconButton(
                                onClick = { onEvent(PokedexStore.Intent.UpdateSearchValue("")) }
                            ) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        disabledContainerColor = containerColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.surface,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.surface,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.surface,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.surface,
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = .6f),
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = .4f),
                    )
                }

                val query = state.searchValue.trim()
                val filteredList = if (query.isEmpty()) state.pokemonList
                else state.pokemonList.filter { it.name.contains(query, ignoreCase = true) }

                if (filteredList.isEmpty() && query.isNotEmpty() && !state.isLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "No Pokemon match \"$query\"",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    PokemonGrid(
                        onPokemonClicked = { name ->
                            onOutput(PokedexComponent.Output.NavigateToDetails(name = name))
                        },
                        pokemonList = filteredList,
                        isLoading = query.isEmpty() && !state.isLastPageLoaded,
                        loadMoreItems = {
                            if (state.pokemonList.isEmpty()) return@PokemonGrid

                            val nextPage = state.pokemonList.last().page + 1
                            onEvent(PokedexStore.Intent.LoadPokemonListByPage(page = nextPage))
                        }
                    )
                }
            }


        }
    }
}
