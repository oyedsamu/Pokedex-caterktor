package com.mocoding.pokedex.ui.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.mocoding.pokedex.ui.details.DetailsScreen
import com.mocoding.pokedex.ui.evolutions.EvolutionDetailsScreen
import com.mocoding.pokedex.ui.evolutions.EvolutionsScreen
import com.mocoding.pokedex.ui.favorite.FavoriteScreen
import com.mocoding.pokedex.ui.locations.LocationDetailsScreen
import com.mocoding.pokedex.ui.locations.LocationsScreen
import com.mocoding.pokedex.ui.main.MainScreen
import com.mocoding.pokedex.ui.moves.MoveDetailsScreen
import com.mocoding.pokedex.ui.moves.MovesScreen
import com.mocoding.pokedex.ui.pokedex.PokedexScreen
import com.mocoding.pokedex.ui.watch.WatchDetailsScreen

@Composable
internal fun RootContent(component: RootComponent) {
    Children(
        stack = component.childStack,
        animation = stackAnimation(fade()),
    ) {
        when(val child = it.instance) {
            is RootComponent.Child.Main -> MainScreen(child.component)
            is RootComponent.Child.Pokedex -> PokedexScreen(child.component)
            is RootComponent.Child.Favorite -> FavoriteScreen(child.component)
            is RootComponent.Child.Details -> DetailsScreen(child.component)
            is RootComponent.Child.Moves -> MovesScreen(child.component)
            is RootComponent.Child.MoveDetails -> MoveDetailsScreen(child.component)
            is RootComponent.Child.Evolutions -> EvolutionsScreen(child.component)
            is RootComponent.Child.EvolutionDetails -> EvolutionDetailsScreen(child.component)
            is RootComponent.Child.Locations -> LocationsScreen(child.component)
            is RootComponent.Child.LocationDetails -> LocationDetailsScreen(child.component)
            is RootComponent.Child.WatchDetails -> WatchDetailsScreen(child.component)
        }
    }
}
