package com.example.pokemon.presentation.feature.pokemon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.domain.usecase.PokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val pokemonUseCase: PokemonUseCase,
) : ViewModel() {

    internal fun getPokemonStateFlow(id: String): Flow<PokemonState> {
        return pokemonUseCase(id).map { result ->
            when (result) {
                is RequestResource.Loading -> PokemonState.Loading
                is RequestResource.Success -> PokemonState.Show(result.data!!)
                is RequestResource.Error -> PokemonState.TryAgain(result.message!!)
            }
        }
    }
}