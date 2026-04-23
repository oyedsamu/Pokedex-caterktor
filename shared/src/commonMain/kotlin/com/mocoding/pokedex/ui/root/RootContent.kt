package com.mocoding.pokedex.ui.root

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.mocoding.pokedex.ui.details.DetailsScreen
import com.mocoding.pokedex.ui.evolutions.EvolutionDetailsScreen
import com.mocoding.pokedex.ui.evolutions.EvolutionsScreen
import com.mocoding.pokedex.ui.favorite.FavoriteScreen
import com.mocoding.pokedex.ui.helper.LocalAnimatedVisibilityScope
import com.mocoding.pokedex.ui.helper.LocalSharedTransitionScope
import com.mocoding.pokedex.ui.locations.LocationDetailsScreen
import com.mocoding.pokedex.ui.locations.LocationsScreen
import com.mocoding.pokedex.ui.main.MainScreen
import com.mocoding.pokedex.ui.moves.MoveDetailsScreen
import com.mocoding.pokedex.ui.moves.MovesScreen
import com.mocoding.pokedex.ui.pokedex.PokedexScreen
import com.mocoding.pokedex.ui.watch.WatchDetailsScreen

@Composable
internal fun RootContent(component: RootComponent) {
    val childStack by component.childStack.subscribeAsState()

    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            AnimatedContent(
                targetState = childStack,
                transitionSpec = {
                    val isForward = targetState.backStack.size >= initialState.backStack.size
                    if (isForward) {
                        (fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.94f))
                            .togetherWith(fadeOut(tween(220)))
                    } else {
                        fadeIn(tween(220))
                            .togetherWith(fadeOut(tween(300)) + scaleOut(tween(300), targetScale = 0.94f))
                    }
                },
                contentKey = { it.active.configuration }
            ) { stack ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    when (val child = stack.active.instance) {
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
        }
    }
}
