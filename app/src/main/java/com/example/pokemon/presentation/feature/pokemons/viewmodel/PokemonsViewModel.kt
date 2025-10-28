package com.example.pokemon.presentation.feature.pokemons.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.domain.usecase.PokemonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PokemonsViewModel @Inject constructor(
    pokemonUseCase: PokemonsUseCase,
) : ViewModel() {

    internal val pokemonsState = pokemonUseCase()
        .map { result ->
            when (result) {
                is RequestResource.Loading -> PokemonsState.Loading
                is RequestResource.Success -> PokemonsState.Show(result.data!!)
                is RequestResource.Error -> PokemonsState.TryAgain(result.message!!)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PokemonsState.Loading
        )
}