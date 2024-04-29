package com.example.pokemon.presentation.feature.pokemon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.domain.usecase.PokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val pokemonUseCase: PokemonUseCase,
) : ViewModel() {
    private val _pokemonState = MutableStateFlow<PokemonState>(PokemonState.Loading)
    internal val pokemonState = _pokemonState.asStateFlow()

    internal fun on(event: PokemonEvent) {
        when (event) {
            is PokemonEvent.Find -> {
                getPokemon(event.id)
            }
        }
    }

    private fun getPokemon(id: String) {
        pokemonUseCase(id).onEach { currentResult ->

            when (currentResult) {
                is RequestResource.Success -> {
                    _pokemonState.value = PokemonState.Show(currentResult.data!!)
                }
                is RequestResource.Error -> {
                    _pokemonState.value = PokemonState.TryAgain(currentResult.message!!)
                }
                is RequestResource.Loading -> {
                    _pokemonState.value = PokemonState.Loading
                }
            }
        }.launchIn(viewModelScope)
    }
}