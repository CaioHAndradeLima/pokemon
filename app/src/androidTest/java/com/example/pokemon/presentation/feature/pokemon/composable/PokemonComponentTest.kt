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

class PokemonComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testCatDetailComponent() {
        val pokemon = provideDefaultPokemonTest()

        composeTestRule.setContent {
            PokemonComponent(pokemon, scrollAlphaListener = { })
        }

        composeTestRule.onNodeWithText(pokemon.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(pokemon.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(
                R.string.base_experience,
                pokemon.baseExperience.toString()
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(
                R.string.height,
                pokemon.height.toString()
            )
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            context.getString(
                R.string.weight,
                pokemon.weight.toString()
            )
        ).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.specie, pokemon.species!!.name))
            .assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.movements)).assertExists()
        composeTestRule.onNodeWithText(pokemon.moves!!.first().move.name).assertExists()
    }
}
