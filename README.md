<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

## Pokemon


[![Pokemon](https://github.com/CaioHAndradeLima/Pokemon/actions/workflows/android.yml/badge.svg)](https://github.com/CaioHAndradeLima/Pokemon/actions/workflows/android.yml)
![Coverage](.github/badges/jacoco.svg)

✓ JetpackCompose
✓ Flow
✓ Retrofit
✓ RxJava/Kotlin
✓ StateFlow
✓ SharedFlow
✓ LiveData
✓ Coroutines
✓ Clean Architecture

<table>
  <tr>
    <td align="center">
      Main Page
      <br>
      <img src="https://github.com/user-attachments/assets/e25634a6-e317-4ab7-8b15-e5084aba3eff" height="300" style="margin-right: 20px;" />
    </td>
    <td align="center">
      Details Page
      <br>
      <img src="https://github.com/user-attachments/assets/d90805af-676a-4925-bbea-3fd89ac8b932" height="300" />
    </td>
  </tr>
</table>

## 4 Branches: One result

this project has 4 different branches addressing concepts such as HotFlow, ColdFlow, UnitTests,
EspressoTests and CI/CD containing each branch a different way to implement using the most used
android libraries in the world such as


| Branch                      | implementation using |
|-----------------------------|----------------------|
| main                        | MutableStateFlow     |
| feature/rx                  | RxJava               |
| feature/live-data-corotines | LiveData             |
| feature/cold_flow           | StateFlow            |

## See the code yourself

<h3><b>1. Branch Main</b></h3>
implementation using MutableStateFlow and Flow to thread management
<details>
  <summary>ViewModel layer</summary>

  ```kotlin
@HiltViewModel
class PokemonsViewModel @Inject constructor(
    private val pokemonUseCase: PokemonsUseCase,
) : ViewModel() {
    private val _pokemonsState = MutableStateFlow<PokemonsState>(PokemonsState.Loading)
    internal val pokemonsState = _pokemonsState.asStateFlow()

    init {
        on(PokemonsEvent.StartRequest)
    }

    internal fun on(event: PokemonsEvent) {
        when (event) {
            is PokemonsEvent.StartRequest -> {
                getPokemons()
            }
        }
    }

    private fun getPokemons() {
        pokemonUseCase().onEach { currentResult ->

            when (currentResult) {
                is RequestResource.Success -> {
                    _pokemonsState.value = PokemonsState.Show(currentResult.data!!)
                }
                is RequestResource.Error -> {
                    _pokemonsState.value = PokemonsState.TryAgain(currentResult.message!!)
                }
                is RequestResource.Loading -> {
                    _pokemonsState.value = PokemonsState.Loading
                }
            }
        }.launchIn(viewModelScope)
    }
}
```

</details> 

<details>
  <summary>UseCase layer</summary>

  ```kotlin
class PokemonsUseCase(
    private val repository: PokemonApiRepository
) {

    operator fun invoke(): Flow<RequestResource<List<Pokemon>>> = flow {
        emit(RequestResource.Loading())

        when (val pokemons = repository.getPokemons()) {
            is ResponseApi.Success -> {
                emit(
                    RequestResource.Success(
                        pokemons.data
                            .filter { it.sprites?.hasPicture() == true }
                            .map { it.copy() })
                )
            }

            is ResponseApi.Error -> {
                emit(RequestResource.Error(pokemons.message))
            }
        }
    }
}
```

</details> 

<details>
  <summary>Repository layer</summary>

  ```kotlin
internal class PokemonRemoteRepository @Inject constructor(
    private val api: PokemonApi
) : PokemonApiRepository {

    override suspend fun getPokemon(id: String) = try {
        ResponseApi.Success(
            api.getPokemonById(id)
        )
    } catch (e: HttpException) {
        ResponseApi.Error.Http(e.toErrorMessage())
    } catch (e: IOException) {
        ResponseApi.Error.Connection(UiText.Resource(R.string.check_your_internet_connection))
    }
}
```

</details>

<br>
<h3><b>2. Branch feature/live-data-corotines</b></h3>
MutableLiveData and classic coroutines
<details>
  <summary>ViewModel layer</summary>

  ```kotlin
@HiltViewModel
class PokemonsViewModel @Inject constructor(
    private val pokemonUseCase: PokemonsUseCase,
) : ViewModel() {
    private val _pokemonsState = MutableLiveData<PokemonsState>(PokemonsState.Loading)
    val pokemonsState: LiveData<PokemonsState> = _pokemonsState

    init {
        on(PokemonsEvent.StartRequest)
    }

    fun on(event: PokemonsEvent) {
        when (event) {
            is PokemonsEvent.StartRequest -> getPokemons()
        }
    }

    private fun getPokemons() = viewModelScope.launch {
        _pokemonsState.value = PokemonsState.Loading

        when (val result = pokemonUseCase()) {
            is RequestResource.Success -> {
                _pokemonsState.value = PokemonsState.Show(result.data!!)
            }

            is RequestResource.Error -> {
                _pokemonsState.value = PokemonsState.TryAgain(result.message!!)
            }
        }
    }
}
```

</details> 

<details>
  <summary>UseCase layer</summary>

  ```kotlin
class PokemonsUseCase(
    private val repository: PokemonApiRepository
) {

    suspend operator fun invoke(): RequestResource<List<Pokemon>> =
        when (val pokemons = repository.getPokemons()) {
            is ResponseApi.Success -> {
                RequestResource.Success(
                    pokemons.data
                        .filter { it.sprites?.hasPicture() == true }
                        .map { it.copy() })
            }

            is ResponseApi.Error -> {
                RequestResource.Error(pokemons.message)
            }
        }
}
```

</details> 
<details>
  <summary>Repository layer</summary>

  ```kotlin
internal class PokemonRemoteRepository @Inject constructor(
    private val api: PokemonApi
) : PokemonApiRepository {

    override suspend fun getPokemons() = try {
        val abilityResponse = api.getAbilityResponse(5, 5)
        ResponseApi.Success(
            abilityResponse
                .results
                .map { api.getAbilityDetails(it.url) }
                .flatMap { it.pokemon }
                .mapNotNull { api.getPokemon(it.pokemon.url) }
        )
    } catch (e: HttpException) {
        ResponseApi.Error.Http(e.toErrorMessage())
    } catch (e: IOException) {
        ResponseApi.Error.Connection(UiText.Resource(R.string.check_your_internet_connection))
    }
}
```

</details>
<br>
<h3><b>3. Branch feature/rx </b></h3>
RxJava and MutableStateFlow
<details>
  <summary>ViewModel layer</summary>

  ```kotlin
@HiltViewModel
class PokemonsViewModel @Inject constructor(
    private val pokemonUseCase: PokemonsUseCase,
) : ViewModel() {
    private val _pokemonsState = MutableStateFlow<PokemonsState>(PokemonsState.Loading)
    internal val pokemonsState = _pokemonsState.asStateFlow()

    init {
        on(PokemonsEvent.StartRequest)
    }

    internal fun on(event: PokemonsEvent) {
        when (event) {
            is PokemonsEvent.StartRequest -> {
                getPokemons()
            }
        }
    }

    private fun getPokemons() = viewModelScope.launch {
        pokemonUseCase()
            .asFlow()
            .collect { currentResult ->
                when (currentResult) {
                    is RequestResource.Success -> {
                        _pokemonsState.value = PokemonsState.Show(currentResult.data!!)
                    }

                    is RequestResource.Error -> {
                        _pokemonsState.value = PokemonsState.TryAgain(currentResult.message!!)
                    }

                    is RequestResource.Loading -> {
                        _pokemonsState.value = PokemonsState.Loading
                    }
                }
            }
    }
}
```

</details>
<details>
  <summary>Use case layer</summary>

  ```kotlin
class PokemonsUseCase(
    private val repository: PokemonApiRepository
) {

    operator fun invoke(): Observable<RequestResource<List<Pokemon>>> {
        return Observable.concat(
            Observable.just(RequestResource.Loading()),
            repository.getPokemons()
                .map { response ->
                    when (response) {
                        is ResponseApi.Success -> RequestResource.Success(
                            response.data
                                .filter { it.sprites?.hasPicture() == true }
                                .map { it.copy() }
                        )

                        is ResponseApi.Error -> RequestResource.Error(response.message)
                    }
                }
                .toObservable()
        )
    }
}
```

</details> 

<details>
  <summary>Repository layer</summary>

  ```kotlin
internal class PokemonRemoteRepository @Inject constructor(
    private val api: PokemonApi
) : PokemonApiRepository {

    override fun getPokemons(): Single<ResponseApi<List<Pokemon>>> {
        return Single.fromCallable {
            try {
                val abilityResponse = api.getAbilityResponse(5, 5).blockingGet()
                val abilityDetails = abilityResponse.results
                    .map { api.getAbilityDetails(it.url).blockingGet() }
                val pokemons = abilityDetails
                    .flatMap { it.pokemon }
                    .mapNotNull { api.getPokemon(it.pokemon.url).blockingGet() }

                ResponseApi.Success(pokemons)
            } catch (e: HttpException) {
                ResponseApi.Error.Http(e.toErrorMessage())
            } catch (e: IOException) {
                ResponseApi.Error.Connection(UiText.Resource(R.string.check_your_internet_connection))
            } catch (e: Exception) {
                ResponseApi.Error.Unknown(e, UiText.Dynamic(e.message ?: "Unknown error"))
            }
        }.subscribeOn(Schedulers.io())
    }
}
```

</details>

<h3><b>4. Branch feature/cold_flow </b></h3>
StateFlow, stateIn, collectWithLifeCycle
<details>
  <summary>ViewModel layer</summary>

  ```kotlin
@HiltViewModel
class PokemonsViewModel @Inject constructor(
    pokemonUseCase: PokemonsUseCase,
) : ViewModel() {

    internal val pokemonsState = pokemonUseCase()
        .map { result ->
            when (result) {
                is RequestResource.Loading -> PokemonsState.Loading
                is RequestResource.Success -> PokemonsState.Show(result.data!!)
                is RequestResource.Error -> PokemonsState.TryAgain(result.message!!)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PokemonsState.Loading
        )
}
```

</details>
<details>
  <summary>Use case layer</summary>

  ```kotlin
class PokemonsUseCase(
    private val repository: PokemonApiRepository
) {

    operator fun invoke(): Flow<RequestResource<List<Pokemon>>> = flow {
        emit(RequestResource.Loading())

        when (val pokemons = repository.getPokemons()) {
            is ResponseApi.Success -> {
                emit(
                    RequestResource.Success(
                    pokemons.data
                        .filter { it.sprites?.hasPicture() == true }
                        .map { it.copy() }
                ))
            }

            is ResponseApi.Error -> {
                emit(RequestResource.Error(pokemons.message))
            }
        }
    }.flowOn(Dispatchers.IO)
}
```

</details> 

<details>
  <summary>Repository layer</summary>

  ```kotlin
internal class PokemonRemoteRepository @Inject constructor(
    private val api: PokemonApi
) : PokemonApiRepository {

    override suspend fun getPokemons() = try {
        val abilityResponse = api.getAbilityResponse(5, 5)
        ResponseApi.Success(
            abilityResponse
                .results
                .map { api.getAbilityDetails(it.url) }
                .flatMap { it.pokemon }
                .mapNotNull { api.getPokemon(it.pokemon.url) }
        )
    } catch (e: HttpException) {
        ResponseApi.Error.Http(e.toErrorMessage())
    } catch (e: IOException) {
        ResponseApi.Error.Connection(UiText.Resource(R.string.check_your_internet_connection))
    }

    override suspend fun getPokemon(id: String) = try {
        ResponseApi.Success(
            api.getPokemonById(id)
        )
    } catch (e: HttpException) {
        ResponseApi.Error.Http(e.toErrorMessage())
    } catch (e: IOException) {
        ResponseApi.Error.Connection(UiText.Resource(R.string.check_your_internet_connection))
    }
}
```

</details>

## 🔄 Continuous integration enabled

[![Pokemon](https://github.com/CaioHAndradeLima/Pokemon/actions/workflows/android.yml/badge.svg)](https://github.com/CaioHAndradeLima/Pokemon/actions/workflows/android.yml)
![Coverage](.github/badges/jacoco.svg)

‍💻 **Code Push** → Triggers Continuous Integration <br>
🧪 **Run Build** → Executes `./gradlew assemble` <br>
🧹 **Run Unit Tests** → Runs Unit tests and fail when tests fail <br>
🏗️  **Run Espresso Tests in real Device** → Runs Espresso tests in real device and fail when tests fail <br>


[See Workflow here](https://github.com/CaioHAndradeLima/pokemon/blob/main/.github/workflows/android.yml)

## Architecture Directory Organization

| Directory    | description                                         |
|--------------|-----------------------------------------------------|
| Common       | Common files for any module/package                 |
| Data         | Define contract interface and repository layer      |
| Domain       | Repository implementation and application use cases |
| Presentation | UI Layer and ViewModel                              |
| di           | Dependency Injection setup                          |
| theme        | Define colors, styles and more                      |

<details>
  <summary>Directory Structure</summary>

  ```text
📂 presentation
│
└── 📂 feature
    ├── 📂 pokemon
    │   ├── 📂 composable
    │   └── 📂 viewmodel
    │
    └── 📂 pokemons
        ├── 📂 composable
        └── 📂 viewmodel

📂 domain
└── 📂 usecase

📂 data
├── 📂 model
└── 📂 repository

📂 common
├── 📂 extension
├── 📂 network
├── 📂 resource
├── 📂 route
└── 📂 ui

📂 ui
└── 📂 theme

```

</details>

</body>
</html>
