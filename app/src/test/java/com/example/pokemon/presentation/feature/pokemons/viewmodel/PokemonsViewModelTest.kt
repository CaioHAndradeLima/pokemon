package com.example.pokemon.presentation.feature.pokemons.viewmodel

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.domain.usecase.PokemonsUseCase
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonsViewModelTest {

    private lateinit var pokemonsUseCase: PokemonsUseCase
    private lateinit var pokemonsViewModel: PokemonsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        pokemonsUseCase = mockk(relaxed = true)
        pokemonsViewModel = PokemonsViewModel(pokemonsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        clearAllMocks()
    }

    @Test
    fun `should update pokemonsState on successful pokemon list retrieval`() = runTest {
        // Arrange
        val pokemonListMock = listOf(provideDefaultPokemonTest())
        val successResult = RequestResource.Success(pokemonListMock)
        coEvery { pokemonsUseCase() } returns flowOf(successResult)

        // Act
        pokemonsViewModel.on(PokemonsEvent.StartRequest)

        // Assert
        assertTrue(pokemonsViewModel.pokemonsState.value is PokemonsState.Show)
        assertEquals(pokemonListMock, (pokemonsViewModel.pokemonsState.value as PokemonsState.Show).pokemons)
    }

    @Test
    fun `should update pokemonsState on error during pokemon list retrieval`() = runTest {
        // Arrange
        val uiText = UiText.Dynamic("Some error message")
        val errorResult = RequestResource.Error<List<Pokemon>>(message = uiText)
        coEvery { pokemonsUseCase() } returns flowOf(errorResult)

        // Act
        pokemonsViewModel.on(PokemonsEvent.StartRequest)

        // Assert
        assertTrue(pokemonsViewModel.pokemonsState.value is PokemonsState.TryAgain)
        assertEquals(uiText, (pokemonsViewModel.pokemonsState.value as PokemonsState.TryAgain).errorMessage)
    }

    @Test
    fun `should update pokemonsState on loading during pokemon list retrieval`() = runTest {
        // Arrange
        val loadingResult = RequestResource.Loading<List<Pokemon>>()
        coEvery { pokemonsUseCase() } returns flowOf(loadingResult)

        // Act
        pokemonsViewModel.on(PokemonsEvent.StartRequest)

        // Assert
        assertTrue(pokemonsViewModel.pokemonsState.value is PokemonsState.Loading)
    }
}
