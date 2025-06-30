package com.example.pokemon.presentation.feature.pokemons.viewmodel

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.domain.usecase.PokemonsUseCase
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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
        every { pokemonsUseCase() } returns Observable.just(RequestResource.Success(pokemonListMock))

        // Act
        pokemonsViewModel.on(PokemonsEvent.StartRequest)
        advanceUntilIdle()

        // Assert
        assert(pokemonsViewModel.pokemonsState.value is PokemonsState.Show)
        assertEquals(
            pokemonListMock,
            (pokemonsViewModel.pokemonsState.value as PokemonsState.Show).pokemons
        )
    }

    @Test
    fun `should update pokemonsState on error during pokemon list retrieval`() = runTest {
        // Arrange
        val uiText = UiText.Dynamic("Some error message")
        every { pokemonsUseCase() } returns Observable.just(RequestResource.Error(message = uiText))

        // Act
        pokemonsViewModel.on(PokemonsEvent.StartRequest)
        advanceUntilIdle()

        // Assert
        assert(pokemonsViewModel.pokemonsState.value is PokemonsState.TryAgain)
        assertEquals(
            uiText,
            (pokemonsViewModel.pokemonsState.value as PokemonsState.TryAgain).errorMessage
        )
    }

    @Test
    fun `should update pokemonsState on loading during pokemon list retrieval`() = runTest {
        // Arrange
        every { pokemonsUseCase() } returns Observable.just(RequestResource.Loading())

        // Act
        pokemonsViewModel.on(PokemonsEvent.StartRequest)
        advanceUntilIdle()

        // Assert
        assert(pokemonsViewModel.pokemonsState.value is PokemonsState.Loading)
    }
}
