package com.example.pokemon.common.route

sealed class ScreenRoute(private val initialRoute: String, private val pathRoute: String? = null) {
    object Pokemons: ScreenRoute("pokemons")
    object Pokemon: ScreenRoute("pokemon", pathRoute = "{id}")

    internal fun route(): String {
        pathRoute?.let { return@route return "$initialRoute/$pathRoute"; }

        return initialRoute
    }

    internal fun buildRoute(vararg params: String): String {
        val pathParams = params.reduce { before, now -> "$before/$now" }
        return "$initialRoute/$pathParams"
    }
}
