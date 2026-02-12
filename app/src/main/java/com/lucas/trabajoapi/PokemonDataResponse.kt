package com.lucas.trabajoapi

import com.google.gson.annotations.SerializedName

// Datos concretos de un Pokémon
data class PokemonResponse(
    @SerializedName("species") val species: SpeciesResponse,
    @SerializedName("sprites") val sprites: SpritesResponse,
    @SerializedName("types") val types: List<TypeSlotResponse>
)

data class SpeciesResponse(
    @SerializedName("name") val name: String
)

data class SpritesResponse(
    @SerializedName("front_default") val frontDefault: String
)

data class TypeSlotResponse(
    @SerializedName("slot") val slot: Int,
    @SerializedName("type") val type: TypeResponse
)

data class TypeResponse(
    @SerializedName("name") val name: String
)


// Lista de los pokemon con la paginación para optimizar
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String
)
