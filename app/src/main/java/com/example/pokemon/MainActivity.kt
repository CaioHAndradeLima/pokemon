package com.example.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokemon.common.route.ScreenRoute
import com.example.pokemon.presentation.feature.pokemon.composable.PokemonScreen
import com.example.pokemon.presentation.feature.pokemons.composable.PokemonsScreen
import com.example.pokemon.ui.theme.PokemonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupNavHost()
                }
            }
        }
    }

    @Composable
    fun SetupNavHost() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = ScreenRoute.Pokemons.route()
        ) {
            composable(route = ScreenRoute.Pokemons.route()) {
                PokemonsScreen(
                    onClick = {
                        navController.navigate(ScreenRoute.Pokemon.buildRoute(it.id.toString()))
                    },
                )
            }
            composable(route = ScreenRoute.Pokemon.route()) {
                val id = it.arguments?.getString("id")!!
                PokemonScreen(id, navController)
            }
        }
    }
}
