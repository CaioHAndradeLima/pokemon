package com.example.pokemon.presentation.feature.pokemon.composable

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pokemon.R
import com.example.pokemon.common.ui.CoilImageComponent
import com.example.pokemon.common.ui.FlowRow
import com.example.pokemon.common.ui.TagComponent
import com.example.pokemon.data.model.Pokemon

@Composable
fun PokemonComponent(
    pokemon: Pokemon,
    scrollAlphaListener: (newAlpha: Float) -> Unit
) {
    val isHorizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .onGloballyPositioned { coordinates ->
                if (isHorizontal) {
                    scrollAlphaListener(1.toFloat())
                    return@onGloballyPositioned
                }

                var value = coordinates
                    .positionInParent()
                    .getDistance() / 10
                if (value > 20) {
                    if (value > 100) {
                        value = 100F
                    }
                    scrollAlphaListener(((value * 0.01).toFloat()))
                }
            }
    ) {
        if (isHorizontal) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 72.dp, start = 16.dp)
            ) {
                Column {
                    val width = (LocalConfiguration.current.screenHeightDp * 0.9)
                    CoilImageComponent(
                        imageUrl = pokemon.sprites!!.bestFrontImageUrl!!,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(shape = RoundedCornerShape(4.dp))
                            .width(width.dp)
                            .height(width.dp),
                        boxModifier = Modifier
                            .clip(shape = RoundedCornerShape(4.dp))
                            .width(width.dp)
                            .height(width.dp),
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    PokemonDetailComponent(pokemon)
                }
            }
        } else {
            CoilImageComponent(
                imageUrl = pokemon.sprites!!.bestFrontImageUrl!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .height((LocalConfiguration.current.screenWidthDp * 1.30).dp),
                contentScale = ContentScale.FillBounds,
                boxModifier = Modifier
                    .fillMaxWidth()
                    .height((LocalConfiguration.current.screenWidthDp * 1.30).dp)
                    .clip(shape = RoundedCornerShape(bottomEnd = 4.dp, bottomStart = 4.dp))
                    .background(Color.Gray),
            )

            PokemonDetailComponent(pokemon = pokemon)
        }

        pokemon.moves?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.movements),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Surface(modifier = Modifier.padding(horizontal = 16.dp)) {
                FlowRow(horizontalGap = 8.dp, verticalGap = 8.dp) {
                    it.forEach {
                        TagComponent(
                            tagText = it.move.name
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}