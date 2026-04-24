package com.mocoding.pokedex.ui.evolutions.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.mocoding.pokedex.core.model.Pokemon
import com.mocoding.pokedex.data.repository.PokemonRepository
import com.mocoding.pokedex.pokedexDispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class EvolutionsStoreFactory(
    private val storeFactory: StoreFactory,
) : KoinComponent {
    private val pokemonRepository by inject<PokemonRepository>()

    fun create(): EvolutionsStore =
        object : EvolutionsStore, Store<EvolutionsStore.Intent, EvolutionsStore.State, Nothing> by storeFactory.create(
            name = "EvolutionsStore",
            initialState = EvolutionsStore.State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed class Msg {
        object Loading : Msg()
        data class Loaded(val pokemon: List<Pokemon>) : Msg()
        data class Failed(val error: String?) : Msg()
        data class SearchUpdated(val value: String) : Msg()
        object LastPageLoaded : Msg()
    }

    private inner class ExecutorImpl : CoroutineExecutor<EvolutionsStore.Intent, Unit, EvolutionsStore.State, Msg, Nothing>(
        pokedexDispatchers.main
    ) {
        override fun executeAction(action: Unit, getState: () -> EvolutionsStore.State) {
            loadPokemonListByPage(0)
        }

        override fun executeIntent(intent: EvolutionsStore.Intent, getState: () -> EvolutionsStore.State) {
            when (intent) {
                is EvolutionsStore.Intent.LoadPokemonListByPage -> loadPokemonListByPage(intent.page, getState().isLastPageLoaded)
                is EvolutionsStore.Intent.UpdateSearchValue -> dispatch(Msg.SearchUpdated(intent.searchValue))
            }
        }

        private var job: Job? = null
        private fun loadPokemonListByPage(page: Long, isLastPageLoaded: Boolean = false) {
            if (job?.isActive == true || isLastPageLoaded) return
            job = scope.launch {
                dispatch(Msg.Loading)
                pokemonRepository.getPokemonList(page)
                    .onSuccess {
                        if (it.isEmpty()) dispatch(Msg.LastPageLoaded)
                        else dispatch(Msg.Loaded(it))
                    }
                    .onFailure { dispatch(Msg.Failed(it.message)) }
            }
        }
    }

    private object ReducerImpl : Reducer<EvolutionsStore.State, Msg> {
        override fun EvolutionsStore.State.reduce(msg: Msg): EvolutionsStore.State =
            when (msg) {
                Msg.Loading -> copy(isLoading = true, error = null)
                is Msg.Loaded -> copy(isLoading = false, pokemonList = pokemonList + msg.pokemon)
                is Msg.Failed -> copy(isLoading = false, error = msg.error)
                is Msg.SearchUpdated -> copy(searchValue = msg.value)
                Msg.LastPageLoaded -> copy(isLoading = false, isLastPageLoaded = true)
            }
    }
}
