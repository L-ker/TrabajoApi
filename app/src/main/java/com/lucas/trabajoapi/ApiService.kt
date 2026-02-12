package com.lucas.trabajoapi

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Lista de Pokémon con paginación
    @GET("pokemon")
    suspend fun getPokemonList(
        //parametros de la api
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PokemonListResponse>

    // Detalles de un Pokémon por nombre
    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(@Path("name") name: String): Response<PokemonResponse>
}
