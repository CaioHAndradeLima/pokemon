<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

## Android project

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


## Package organization

| Packages     | description
|--------------|------------------------------
| Common       | Common files for any module/package                            
| Data         | Define contract interface and repository layer                           
| Domain       | Repository implementation and application use cases                  
| Presentation | UI Layer and ViewModel (business logic)                            
| di           | Dependency Injection setup                         
| theme        | Define colors, styles and more                            


## Architecture organization

Presentation <-> Bloc <-> UseCase <-> Repository

## App Features

| Feature      | Description
|--------------|------------------------------
| Pokemons     | Show a list of pokemons
| Pokemon      | Show pockemon details

</body>
</html>
