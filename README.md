<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

## Android Pokemon project

this project has 3 different versions of most used android library in the world such as Flow/Rx/LiveData/corotines.


| Branch                       | implementation
|-------------------------------|------------------------------
| main               | Kotlin Flow
| feature/rx               | RxJava
| feature/live-data-corotines               | Corotines and live data

## Screenshots

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

## Pokemon


| Feature                       | implementation
|-------------------------------|------------------------------
| Jetpack Compose               | ✓
| Kotlin Flow                   | ✓
| Corotines                     | ✓
| Live data                     | ✓
| RxJava                        | ✓
| Local data persistance        | ✘
| Infinite Scroll or pagination | ✘                            
| Detail Screen                 | ✓                            
| Transition Animation          | ✘                            
| Image Internal Cache          | ✓                            
| Horizontal adaptation         | ✓                            
| Dependency Injection          | ✓                            
| Good Test Coverage            | ✓                            
| Unit Tests                    | ✓                            
| UI Tests                      | ✓                            


## Package organization

| Packages     | description
|--------------|------------------------------
| Common       | Common files for any module/package                            
| Data         | Define contract interface and repository layer                           
| Domain       | Repository implementation and application use cases                  
| Presentation | UI Layer and ViewModel                
| di           | Dependency Injection setup                         
| theme        | Define colors, styles and more                            


## Architecture organization

Presentation <-> ViewModel <-> UseCase <-> Repository

## App Features

| Feature      | Description
|--------------|------------------------------
| Pokemons     | Show a list of pokemons
| Pokemon      | Show pokemon details


## See the code yourself

<h3><b>1. Branch using Flow and corotines</b></h3>

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
<h3><b>2. Branch using Live data and corotines</b></h3>
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
<h3><b>3. Branch using RxJava and MutableStateFlow</b></h3>

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

## Continuous integration enabled

any pull request opened will run the build, unit tests and espresso tests. See the [CI algoritm here](https://github.com/CaioHAndradeLima/pokemon/blob/main/.github/workflows/android.yml)

## Hybrid experience

I have experience on flutter apps, [see here](https://github.com/CaioHAndradeLima/nasa)

## Nest JS experience

I have experience creating Nest JS api, [see here](https://github.com/CaioHAndradeLima/nasa-api)

## Other Apps

if you would like to see more Android apps experience, see this other project with local persistence [see this project](https://github.com/CaioHAndradeLima/jetpackComposeAssignment)

</body>
</html>
