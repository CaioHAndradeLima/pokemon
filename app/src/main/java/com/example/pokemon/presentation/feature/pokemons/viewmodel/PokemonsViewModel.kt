package com.example.pokemon.presentation.feature.pokemons.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.domain.usecase.PokemonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonsViewModel @Inject constructor(
    private val pokemonUseCase: PokemonsUseCase,
) : ViewModel() {
    private val _pokemonsState = MutableLiveData<PokemonsState>(PokemonsState.Loading)
    val pokemonsState: LiveData<PokemonsState> = _pokemonsState

    init {
        on(PokemonsEvent.StartRequest)
    }

    fun on(event: PokemonsEvent) {
        when (event) {
            is PokemonsEvent.StartRequest -> getPokemons()
        }
    }

    private fun getPokemons() = viewModelScope.launch {
        _pokemonsState.value = PokemonsState.Loading

        when (val result = pokemonUseCase()) {
            is RequestResource.Success -> {
                _pokemonsState.value = PokemonsState.Show(result.data!!)
            }

            is RequestResource.Error -> {
                _pokemonsState.value = PokemonsState.TryAgain(result.message!!)
            }
        }
    }
}