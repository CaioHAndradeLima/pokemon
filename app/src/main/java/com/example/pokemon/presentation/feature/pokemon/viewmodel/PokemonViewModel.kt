package com.example.pokemon.presentation.feature.pokemon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.domain.usecase.PokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val pokemonUseCase: PokemonUseCase,
) : ViewModel() {
    private val _pokemonState = MutableLiveData<PokemonState>(PokemonState.Loading)
    internal val pokemonState: LiveData<PokemonState> = _pokemonState

    internal fun on(event: PokemonEvent) {
        when (event) {
            is PokemonEvent.Find -> {
                getPokemon(event.id)
            }
        }
    }

    private fun getPokemon(id: String) = viewModelScope.launch {
        _pokemonState.value = PokemonState.Loading

        when (val result = pokemonUseCase(id)) {
            is RequestResource.Success -> {
                _pokemonState.value = PokemonState.Show(result.data!!)
            }
            is RequestResource.Error -> {
                _pokemonState.value = PokemonState.TryAgain(result.message!!)
            }
        }
    }
}