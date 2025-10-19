package com.example.pokemon.presentation.feature.pokemons.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pokemon.common.ui.DefaultTopBar
import com.example.pokemon.common.ui.ProgressComponent
import com.example.pokemon.common.ui.TryAgainComponent
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.presentation.feature.pokemons.viewmodel.PokemonsViewModel
import com.example.pokemon.presentation.feature.pokemons.viewmodel.PokemonsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonsScreen(
    onClick: (Pokemon) -> Unit,
    viewModel: PokemonsViewModel = hiltViewModel()
) {
    val state by viewModel.pokemonsState.collectAsState()

    val configuration = LocalConfiguration.current

    val columnsSize by remember(configuration.orientation) {
        mutableStateOf(
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 3 else 2
        )
    }

    Scaffold(
        topBar = { DefaultTopBar() },
        content = { padding ->
            Surface(modifier = Modifier.padding(padding)) {
                when (state) {
                    is PokemonsState.Loading -> ProgressComponent()
                    is PokemonsState.TryAgain -> TryAgainComponent(
                        (state as PokemonsState.TryAgain).errorMessage
                    )

                    is PokemonsState.Show -> LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        columns = GridCells.Fixed(columnsSize)
                    ) {
                        val pokemons = (state as PokemonsState.Show).pokemons
                        items(pokemons.size) { index ->
                            PokemonItem(
                                pokemon = pokemons[index],
                                onItemClick = onClick
                            )
                        }
                    }
                }
            }
        }
    )
}