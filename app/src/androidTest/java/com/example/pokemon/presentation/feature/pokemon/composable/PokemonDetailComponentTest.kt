package com.example.pokemon.presentation.feature.pokemon.composable

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pokemon.R
import com.example.pokemon.provider.provideDefaultPokemonTest
import org.junit.Rule
import org.junit.Test


class PokemonDetailComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testPokemonDetailComposable() {
        val pokemon = provideDefaultPokemonTest()

        composeTestRule.setContent {
            PokemonDetailComponent(pokemon = pokemon)
        }

        composeTestRule.onNodeWithText(pokemon.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.base_experience, pokemon.baseExperience.toString())).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.height, pokemon.height.toString())).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.weight, pokemon.weight.toString())).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.specie, pokemon.species!!.name)).assertExists()
    }
}
