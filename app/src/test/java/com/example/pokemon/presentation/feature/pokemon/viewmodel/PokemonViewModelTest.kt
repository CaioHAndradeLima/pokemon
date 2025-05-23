package com.example.pokemon.presentation.feature.pokemon.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.domain.usecase.PokemonUseCase
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
class PokemonViewModelTest {

    private lateinit var pokemonUseCase: PokemonUseCase
    private lateinit var pokemonViewModel: PokemonViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        pokemonUseCase = mockk(relaxed = true)
        pokemonViewModel = PokemonViewModel(pokemonUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        clearAllMocks()
    }

    @Test
    fun `should update pokemonState to Show on successful pokemon retrieval`() = runTest {
        // Arrange
        val pokemonMock = provideDefaultPokemonTest()
        coEvery { pokemonUseCase(any()) } returns RequestResource.Success(pokemonMock)

        // Act
        pokemonViewModel.on(PokemonEvent.Find("1"))

        // Assert
        assertTrue(pokemonViewModel.pokemonState.value is PokemonState.Show)
        val state = pokemonViewModel.pokemonState.value as PokemonState.Show
        assertEquals(pokemonMock, state.pokemon)
    }

    @Test
    fun `should update pokemonState to TryAgain on error`() = runTest {
        // Arrange
        val errorText = UiText.Dynamic("Some error")
        coEvery { pokemonUseCase(any()) } returns RequestResource.Error(errorText)

        // Act
        pokemonViewModel.on(PokemonEvent.Find("1"))

        // Assert
        assertTrue(pokemonViewModel.pokemonState.value is PokemonState.TryAgain)
        val state = pokemonViewModel.pokemonState.value as PokemonState.TryAgain
        assertEquals(errorText, state.errorMessage)
    }

    @Test
    fun `should update pokemonState to Loading before result`() = runTest {
        // Arrange
        val pokemonMock = provideDefaultPokemonTest()
        val capturedStates = mutableListOf<PokemonState>()
        val observer = Observer<PokemonState> { capturedStates.add(it) }

        coEvery { pokemonUseCase(any()) } coAnswers {
            delay(10)
            RequestResource.Success(pokemonMock)
        }

        pokemonViewModel.pokemonState.observeForever(observer)

        // Act
        pokemonViewModel.on(PokemonEvent.Find("1"))
        advanceUntilIdle()

        // Assert
        assertTrue(capturedStates.first() is PokemonState.Loading)
        assertTrue(capturedStates.last() is PokemonState.Show)
        assertEquals(pokemonMock, (capturedStates.last() as PokemonState.Show).pokemon)

        // Cleanup
        pokemonViewModel.pokemonState.removeObserver(observer)
    }
}
