package com.example.pokemon.provider

import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.model.PokemonMove
import com.example.pokemon.data.model.PokemonMoves
import com.example.pokemon.data.model.PokemonSpecies
import com.example.pokemon.data.model.sprites.HomeSprites
import com.example.pokemon.data.model.sprites.OfficialSprites
import com.example.pokemon.data.model.sprites.PokemonSprites

fun provideDefaultPokemonTest() = Pokemon(
    id = 1,
    name = "Bulbasaur",
    baseExperience = 64,
    height = 7,
    isDefault = true,
    order = 1,
    weight = 69,
    sprites = PokemonSprites(
        frontDefault = "https://pokeapi.co/api/v2/pokemon/1/official-artwork.png",
        artwork = OfficialSprites("https://pokeapi.co/api/v2/pokemon/1/official-artwork.png"),
        home = HomeSprites(
            frontDefault = "https://pokeapi.co/api/v2/pokemon/1/home.png",
            frontFemale = "https://pokeapi.co/api/v2/pokemon/1/home.png",
            frontShiny = "https://pokeapi.co/api/v2/pokemon/1/home.png",
        )
    ),
    species = PokemonSpecies(
        name = "Picachu",
        url = "url_mock",
    ),
    moves = listOf(
        PokemonMoves(
            move = PokemonMove(
                name = "stomp",
                url = "url_mock",
            )
        )
    )
)

fun providePokemonWithoutPictureTest() = Pokemon(
    id = 1,
    name = "Bulbasaur",
    baseExperience = 64,
    height = 7,
    isDefault = true,
    order = 1,
    weight = 69,
    sprites = null,
    species = PokemonSpecies(
        name = "Picachu",
        url = "url_mock",
    ),
    moves = listOf(
        PokemonMoves(
            move = PokemonMove(
                name = "stomp",
                url = "url_mock",
            )
        )
    )
)
