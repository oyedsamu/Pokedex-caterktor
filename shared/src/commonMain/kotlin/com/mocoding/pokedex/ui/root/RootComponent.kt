package com.mocoding.pokedex.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.mocoding.pokedex.ui.details.DetailsComponent
import com.mocoding.pokedex.ui.evolutions.EvolutionDetailsComponent
import com.mocoding.pokedex.ui.evolutions.EvolutionsComponent
import com.mocoding.pokedex.ui.favorite.FavoriteComponent
import com.mocoding.pokedex.ui.locations.LocationDetailsComponent
import com.mocoding.pokedex.ui.locations.LocationsComponent
import com.mocoding.pokedex.ui.main.MainComponent
import com.mocoding.pokedex.ui.moves.MoveDetailsComponent
import com.mocoding.pokedex.ui.moves.MovesComponent
import com.mocoding.pokedex.ui.pokedex.PokedexComponent
import com.mocoding.pokedex.ui.watch.WatchDetailsComponent

class RootComponent internal constructor(
    componentContext: ComponentContext,
    private val main: (ComponentContext, (MainComponent.Output) -> Unit) -> MainComponent,
    private val pokedex: (ComponentContext, searchValue: String, (PokedexComponent.Output) -> Unit) -> PokedexComponent,
    private val favorite: (ComponentContext, (FavoriteComponent.Output) -> Unit) -> FavoriteComponent,
    private val details: (ComponentContext, pokemonName: String, (DetailsComponent.Output) -> Unit) -> DetailsComponent,
    private val moves: (ComponentContext, (MovesComponent.Output) -> Unit) -> MovesComponent,
    private val moveDetails: (ComponentContext, moveName: String, (MoveDetailsComponent.Output) -> Unit) -> MoveDetailsComponent,
    private val evolutions: (ComponentContext, (EvolutionsComponent.Output) -> Unit) -> EvolutionsComponent,
    private val evolutionDetails: (ComponentContext, pokemonName: String, (EvolutionDetailsComponent.Output) -> Unit) -> EvolutionDetailsComponent,
    private val locations: (ComponentContext, (LocationsComponent.Output) -> Unit) -> LocationsComponent,
    private val locationDetails: (ComponentContext, locationAreaName: String, (LocationDetailsComponent.Output) -> Unit) -> LocationDetailsComponent,
    private val watchDetails: (ComponentContext, videoId: String, (WatchDetailsComponent.Output) -> Unit) -> WatchDetailsComponent,
): ComponentContext by componentContext {

    constructor(
        componentContext: ComponentContext,
        storeFactory: StoreFactory,
    ) : this(
        componentContext = componentContext,
        main = { childContext, output ->
            MainComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                output = output
            )
        },
        pokedex = { childContext, searchValue, output ->
            PokedexComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                searchValue = searchValue,
                output = output
            )
        },
        favorite = { childContext, output ->
            FavoriteComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                output = output
            )
        },
        details = { childContext, pokemonName, output ->
            DetailsComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                pokemonName = pokemonName,
                output = output
            )
        },
        moves = { childContext, output ->
            MovesComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                output = output
            )
        },
        moveDetails = { childContext, moveName, output ->
            MoveDetailsComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                moveName = moveName,
                output = output,
            )
        },
        evolutions = { childContext, output ->
            EvolutionsComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                output = output,
            )
        },
        evolutionDetails = { childContext, pokemonName, output ->
            EvolutionDetailsComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                pokemonName = pokemonName,
                output = output,
            )
        },
        locations = { childContext, output ->
            LocationsComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                output = output,
            )
        },
        locationDetails = { childContext, locationAreaName, output ->
            LocationDetailsComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                locationAreaName = locationAreaName,
                output = output,
            )
        },
        watchDetails = { childContext, videoId, output ->
            WatchDetailsComponent(
                componentContext = childContext,
                storeFactory = storeFactory,
                videoId = videoId,
                output = output,
            )
        },
    )

    private val navigation = StackNavigation<Configuration>()

    private val stack =
        childStack(
            source = navigation,
            initialConfiguration = Configuration.Main,
            handleBackButton = false,
            childFactory = ::createChild
        )

    val childStack: Value<ChildStack<*, Child>> = stack

    private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
        when (configuration) {
            is Configuration.Main -> Child.Main(main(componentContext, ::onMainOutput))
            is Configuration.Pokedex -> Child.Pokedex(pokedex(componentContext, configuration.searchValue, ::onPokedexOutput))
            is Configuration.Favorite -> Child.Favorite(favorite(componentContext, ::onFavoriteOutput))
            is Configuration.Details -> Child.Details(details(componentContext, configuration.pokemonName, ::onDetailsOutput))
            is Configuration.Moves -> Child.Moves(moves(componentContext, ::onMovesOutput))
            is Configuration.MoveDetails -> Child.MoveDetails(moveDetails(componentContext, configuration.moveName, ::onMoveDetailsOutput))
            is Configuration.Evolutions -> Child.Evolutions(evolutions(componentContext, ::onEvolutionsOutput))
            is Configuration.EvolutionDetails -> Child.EvolutionDetails(evolutionDetails(componentContext, configuration.pokemonName, ::onEvolutionDetailsOutput))
            is Configuration.Locations -> Child.Locations(locations(componentContext, ::onLocationsOutput))
            is Configuration.LocationDetails -> Child.LocationDetails(locationDetails(componentContext, configuration.locationAreaName, ::onLocationDetailsOutput))
            is Configuration.WatchDetails -> Child.WatchDetails(watchDetails(componentContext, configuration.videoId, ::onWatchDetailsOutput))
        }

    private fun onMainOutput(output: MainComponent.Output): Unit =
        when (output) {
            MainComponent.Output.PokedexClicked -> navigation.push(Configuration.Pokedex())
            MainComponent.Output.FavoriteClicked -> navigation.push(Configuration.Favorite)
            MainComponent.Output.MovesClicked -> navigation.push(Configuration.Moves)
            MainComponent.Output.EvolutionsClicked -> navigation.push(Configuration.Evolutions)
            MainComponent.Output.LocationsClicked -> navigation.push(Configuration.Locations)
            is MainComponent.Output.WatchClicked -> navigation.push(Configuration.WatchDetails(output.videoId))
            is MainComponent.Output.PokedexSearchSubmitted -> navigation.push(Configuration.Pokedex(output.searchValue))
        }

    private fun onPokedexOutput(output: PokedexComponent.Output): Unit =
        when (output) {
            is PokedexComponent.Output.NavigateBack -> navigation.pop()
            is PokedexComponent.Output.NavigateToDetails -> navigation.push(Configuration.Details(output.name))
        }

    private fun onFavoriteOutput(output: FavoriteComponent.Output): Unit =
        when (output) {
            is FavoriteComponent.Output.NavigateBack -> navigation.pop()
            is FavoriteComponent.Output.NavigateToDetails -> navigation.push(Configuration.Details(output.name))
        }

    private fun onDetailsOutput(output: DetailsComponent.Output): Unit =
        when (output) {
            is DetailsComponent.Output.NavigateBack -> navigation.pop()
        }

    private fun onMovesOutput(output: MovesComponent.Output): Unit =
        when (output) {
            is MovesComponent.Output.NavigateBack -> navigation.pop()
            is MovesComponent.Output.NavigateToMoveDetails -> navigation.push(Configuration.MoveDetails(output.moveName))
        }

    private fun onMoveDetailsOutput(output: MoveDetailsComponent.Output): Unit =
        when (output) {
            is MoveDetailsComponent.Output.NavigateBack -> navigation.pop()
        }

    private fun onEvolutionsOutput(output: EvolutionsComponent.Output): Unit =
        when (output) {
            is EvolutionsComponent.Output.NavigateBack -> navigation.pop()
            is EvolutionsComponent.Output.NavigateToEvolutionDetails -> navigation.push(Configuration.EvolutionDetails(output.pokemonName))
        }

    private fun onEvolutionDetailsOutput(output: EvolutionDetailsComponent.Output): Unit =
        when (output) {
            is EvolutionDetailsComponent.Output.NavigateBack -> navigation.pop()
        }

    private fun onLocationsOutput(output: LocationsComponent.Output): Unit =
        when (output) {
            is LocationsComponent.Output.NavigateBack -> navigation.pop()
            is LocationsComponent.Output.NavigateToLocationDetails -> navigation.push(Configuration.LocationDetails(output.locationAreaName))
        }

    private fun onLocationDetailsOutput(output: LocationDetailsComponent.Output): Unit =
        when (output) {
            is LocationDetailsComponent.Output.NavigateBack -> navigation.pop()
        }

    private fun onWatchDetailsOutput(output: WatchDetailsComponent.Output): Unit =
        when (output) {
            is WatchDetailsComponent.Output.NavigateBack -> navigation.pop()
        }

    private sealed class Configuration: Parcelable {
        @Parcelize
        object Main : Configuration()

        @Parcelize
        data class Pokedex(val searchValue: String = "") : Configuration()
        @Parcelize
        object Favorite : Configuration()
        @Parcelize
        data class Details(val pokemonName: String) : Configuration()
        @Parcelize
        object Moves : Configuration()
        @Parcelize
        data class MoveDetails(val moveName: String) : Configuration()
        @Parcelize
        object Evolutions : Configuration()
        @Parcelize
        data class EvolutionDetails(val pokemonName: String) : Configuration()
        @Parcelize
        object Locations : Configuration()
        @Parcelize
        data class LocationDetails(val locationAreaName: String) : Configuration()
        @Parcelize
        data class WatchDetails(val videoId: String) : Configuration()
    }

    sealed class Child {
        data class Main(val component: MainComponent) : Child()
        data class Pokedex(val component: PokedexComponent) : Child()
        data class Favorite(val component: FavoriteComponent) : Child()
        data class Details(val component: DetailsComponent) : Child()
        data class Moves(val component: MovesComponent) : Child()
        data class MoveDetails(val component: MoveDetailsComponent) : Child()
        data class Evolutions(val component: EvolutionsComponent) : Child()
        data class EvolutionDetails(val component: EvolutionDetailsComponent) : Child()
        data class Locations(val component: LocationsComponent) : Child()
        data class LocationDetails(val component: LocationDetailsComponent) : Child()
        data class WatchDetails(val component: WatchDetailsComponent) : Child()
    }

}
