package com.example.pokemon.presentation.feature.pokemon.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.example.pokemon.R
import com.example.pokemon.common.ui.ProgressComponent
import com.example.pokemon.common.ui.TryAgainComponent
import com.example.pokemon.presentation.feature.pokemon.viewmodel.PokemonEvent
import com.example.pokemon.presentation.feature.pokemon.viewmodel.PokemonState
import com.example.pokemon.presentation.feature.pokemon.viewmodel.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonScreen(
    id: String,
    navController: NavHostController,
    pokemonViewModel: PokemonViewModel = hiltViewModel(),
) {
    val state = pokemonViewModel.pokemonState.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var alpha by remember { mutableStateOf(0.1F) }
    LaunchedEffect(key1 = id) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            pokemonViewModel.on(PokemonEvent.Find(id))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.pokemon_detail_title), color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.pokemon_detail_title),
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Black.copy(alpha) )
            )
        },
        content = { padding ->
            when(state.value) {
                is PokemonState.Loading -> ProgressComponent()
                is PokemonState.Show -> PokemonComponent((state.value as PokemonState.Show).pokemon) {
                    alpha = it
                }
                is PokemonState.TryAgain -> Surface(modifier = Modifier.padding(padding)) {
                    TryAgainComponent(((state.value as PokemonState.TryAgain).errorMessage))
                }
            }
        }
    )
}
