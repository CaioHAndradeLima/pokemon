package com.example.pokemon.presentation.feature.pokemon.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pokemon.R
import com.example.pokemon.data.model.Pokemon

@Composable
fun PokemonDetailComponent(pokemon: Pokemon) {
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = pokemon.name,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(id = R.string.base_experience, pokemon.baseExperience.toString()),
        textAlign = TextAlign.Justify,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )

    Text(
        text = stringResource(id = R.string.height, pokemon.height.toString()),
        textAlign = TextAlign.Justify,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )

    Text(
        text = stringResource(id = R.string.weight, pokemon.weight.toString()),
        textAlign = TextAlign.Justify,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )

    pokemon.species?.let {
        Text(
            text = stringResource(id = R.string.specie, it.name),
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}