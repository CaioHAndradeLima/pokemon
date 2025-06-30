package com.example.pokemon.presentation.feature.pokemon.composable

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.domain.usecase.PokemonUseCase
import com.example.pokemon.presentation.feature.pokemon.viewmodel.PokemonViewModel
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class PokemonScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testPokemonScreenSuccessState() = runTest {
        val useCase = mockk<PokemonUseCase>()
        val viewModel = PokemonViewModel(useCase)
        val id = "1"
        val pokemon = provideDefaultPokemonTest()

        every { useCase.invoke(id) } returns Observable.just(RequestResource.Success(pokemon))

        composeTestRule.setContent {
            PokemonScreen(id = id, navController = mockk(), pokemonViewModel = viewModel)
        }

        composeTestRule.onNodeWithText(pokemon.name).assertIsDisplayed()
    }

    @Test
    fun testPokemonScreenTryAgainState() = runTest {
        val errorMessage = "Error while loading information"
        val useCase = mockk<PokemonUseCase>()
        val viewModel = PokemonViewModel(useCase)
        val id = "1"
        every { useCase.invoke(id) } returns Observable.just(
            RequestResource.Error(UiText.Dynamic(errorMessage))
        )

        composeTestRule.setContent {
            PokemonScreen(id = id, navController = mockk(), pokemonViewModel = viewModel)
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}
