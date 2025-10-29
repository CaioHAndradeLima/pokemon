package com.example.pokemon.data

import com.example.pokemon.R
import com.example.pokemon.common.resource.UiText

sealed class ResponseApi<out T> {
    data class Success<out T>(val data: T) : ResponseApi<T>()

    sealed class Error<out T>(
        open val message: UiText
    ) : ResponseApi<T>() {

        data class Connection<T>(
            override val message: UiText = UiText.Resource(R.string.check_your_internet_connection)
        ) : Error<T>(message)

        data class Http<T>(
            override val message: UiText
        ) : Error<T>(message)
    }
}
