package com.example.pokemon.presentation.feature.pokemons.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.domain.usecase.PokemonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asFlow
import javax.inject.Inject

@HiltViewModel
class PokemonsViewModel @Inject constructor(
    private val pokemonUseCase: PokemonsUseCase,
) : ViewModel() {
    private val _pokemonsState = MutableStateFlow<PokemonsState>(PokemonsState.Loading)
    internal val pokemonsState = _pokemonsState.asStateFlow()

    init {
        on(PokemonsEvent.StartRequest)
    }

    internal fun on(event: PokemonsEvent) {
        when (event) {
            is PokemonsEvent.StartRequest -> {
                getPokemons()
            }
        }
    }

    private fun getPokemons() = viewModelScope.launch {
        pokemonUseCase()
            .asFlow()
            .collect { currentResult ->
                when (currentResult) {
                    is RequestResource.Success -> {
                        _pokemonsState.value = PokemonsState.Show(currentResult.data!!)
                    }

                    is RequestResource.Error -> {
                        _pokemonsState.value = PokemonsState.TryAgain(currentResult.message!!)
                    }

                    is RequestResource.Loading -> {
                        _pokemonsState.value = PokemonsState.Loading
                    }
                }
            }
    }
}