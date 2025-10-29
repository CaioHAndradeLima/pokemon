package com.example.pokemon.presentation.feature.pokemons.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokemon.common.ui.CoilImageComponent
import com.example.pokemon.data.model.Pokemon

@Composable
fun PokemonItemComponent(
    pokemon: Pokemon,
    onItemClick: (Pokemon) -> Unit,
) {
    Surface(
        modifier = Modifier
            .clickable(
                onClick = {
                    onItemClick(pokemon)
                })
            .padding(horizontal = 8.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            CoilImageComponent(
                imageUrl = pokemon.sprites!!.bestFrontImageUrl!!,
            )
            Spacer(modifier = Modifier
                .width(8.dp)
                .height(8.dp))
            Text(text = pokemon.name)
        }
    }
}