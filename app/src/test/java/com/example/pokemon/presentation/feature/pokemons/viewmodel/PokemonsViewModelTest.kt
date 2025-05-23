package com.example.pokemon.presentation.feature.pokemons.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.domain.usecase.PokemonsUseCase
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonsViewModelTest {

    private lateinit var pokemonsUseCase: PokemonsUseCase
    private lateinit var pokemonsViewModel: PokemonsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        pokemonsUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        clearAllMocks()
    }

    @Test
    fun `should update pokemonsState to Show on successful list retrieval`() = runTest {
        // Arrange
        val pokemonListMock = listOf(provideDefaultPokemonTest())
        coEvery { pokemonsUseCase() } returns RequestResource.Success(pokemonListMock)

        // Act
        pokemonsViewModel = PokemonsViewModel(pokemonsUseCase) // triggers init
        advanceUntilIdle()

        // Assert
        assertTrue(pokemonsViewModel.pokemonsState.value is PokemonsState.Show)
        val state = pokemonsViewModel.pokemonsState.value as PokemonsState.Show
        assertEquals(pokemonListMock, state.pokemons)
    }

    @Test
    fun `should update pokemonsState to TryAgain on error`() = runTest {
        // Arrange
        val uiText = UiText.Dynamic("Some error message")
        coEvery { pokemonsUseCase() } returns RequestResource.Error(uiText)

        // Act
        pokemonsViewModel = PokemonsViewModel(pokemonsUseCase)
        advanceUntilIdle()

        // Assert
        assertTrue(pokemonsViewModel.pokemonsState.value is PokemonsState.TryAgain)
        val state = pokemonsViewModel.pokemonsState.value as PokemonsState.TryAgain
        assertEquals(uiText, state.errorMessage)
    }

    @Test
    fun `should emit Loading state before result`() = runTest {
        // Arrange
        val pokemonListMock = listOf(provideDefaultPokemonTest())
        val observedStates = mutableListOf<PokemonsState>()
        val observer = Observer<PokemonsState> { observedStates.add(it) }

        coEvery { pokemonsUseCase() } coAnswers {
            delay(10)
            RequestResource.Success(pokemonListMock)
        }

        pokemonsViewModel = PokemonsViewModel(pokemonsUseCase)
        pokemonsViewModel.pokemonsState.observeForever(observer)
        advanceUntilIdle()


        // Assert
        assertTrue(observedStates.first() is PokemonsState.Loading)
        assertTrue(observedStates.last() is PokemonsState.Show)
        assertEquals(pokemonListMock, (observedStates.last() as PokemonsState.Show).pokemons)

        // Cleanup
        pokemonsViewModel.pokemonsState.removeObserver(observer)
    }
}
