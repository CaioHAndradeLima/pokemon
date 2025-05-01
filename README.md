<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

## Android Pokemon project

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

<details>
  <summary>View Model implementation</summary>

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
  <summary>Use Case implementation</summary>

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
  <summary>Repository implementation</summary>

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

## Other Apps

if you would like see local persistence on Android App, [see this project](https://github.com/CaioHAndradeLima/jetpackComposeAssignment)


## Hybrid experience

I have experience on flutter apps, [see here](https://github.com/CaioHAndradeLima/nasa)


</body>
</html>
