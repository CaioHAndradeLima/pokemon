package com.example.pokemon.presentation.feature.pokemons.composable

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.provider.provideDefaultPokemonTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


internal class PokemonItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun whenItemClick_ShouldCallOnItemClick() {
        // Given
        var clickedPokemon: Pokemon? = null
        val pokemon = provideDefaultPokemonTest()

        composeTestRule.setContent {
            PokemonItem(
                pokemon = pokemon,
                onItemClick = { clickedPokemon = it }
            )
        }

        // When
        composeTestRule.onNodeWithText(pokemon.name).performClick()

        // Then
        assertEquals(pokemon, clickedPokemon)
    }

    @Test
    fun whenItemShowing_ShouldShowName() {
        val pokemon = provideDefaultPokemonTest()

        composeTestRule.setContent {
            PokemonItem(
                pokemon = pokemon,
                onItemClick = { }
            )
        }

        composeTestRule.onNodeWithText(pokemon.name).assertExists()
    }
}
