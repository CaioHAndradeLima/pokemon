package com.example.pokemon.presentation.feature.pokemons.composable


import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.domain.usecase.PokemonUseCase
import com.example.pokemon.domain.usecase.PokemonsUseCase
import com.example.pokemon.presentation.feature.pokemon.composable.PokemonScreen
import com.example.pokemon.presentation.feature.pokemon.viewmodel.PokemonViewModel
import com.example.pokemon.presentation.feature.pokemons.viewmodel.PokemonsViewModel
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class PokemonsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testPokemonsScreenSuccessState() = runTest {
        val useCase = mockk<PokemonsUseCase>()
        val viewModel = PokemonsViewModel(useCase)
        val pokemon = provideDefaultPokemonTest()

        every { useCase.invoke() } returns flowOf(
            RequestResource.Success(listOf(pokemon))
        )

        composeTestRule.setContent {
            PokemonsScreen(onClick = {}, viewModel = viewModel)
        }

        composeTestRule.onNodeWithText(pokemon.name).assertIsDisplayed()
    }

    @Test
    fun testPokemonsScreenTryAgainState() = runTest {
        val errorMessage = "Error while loading information"
        val useCase = mockk<PokemonsUseCase>()
        val viewModel = PokemonsViewModel(useCase)

        every { useCase.invoke() } returns flowOf(
            RequestResource.Error(
                message = UiText.Dynamic(errorMessage)
            )
        )

        composeTestRule.setContent {
            PokemonsScreen(viewModel = viewModel, onClick = {})
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
