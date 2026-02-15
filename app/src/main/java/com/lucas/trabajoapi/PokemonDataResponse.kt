package com.lucas.trabajoapi

import com.google.gson.annotations.SerializedName

// Datos concretos de un Pokémon
data class PokemonResponse(
    val id: Int,
    @SerializedName("species") val species: SpeciesResponse,
    @SerializedName("sprites") val sprites: SpritesResponse,
    @SerializedName("types") val types: List<TypeSlotResponse>,
    @SerializedName("abilities") val abilities: List<AbilitySlotResponse>,
    @SerializedName("stats") val stats: List<StatSlotResponse>
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

data class AbilitySlotResponse(
    @SerializedName("ability") val ability: AbilityResponse,
    @SerializedName("is_hidden") val isHidden: Boolean,
    @SerializedName("slot") val slot: Int
)

data class AbilityResponse(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

data class StatSlotResponse(
    @SerializedName("base_stat") val baseStat: Int,
    @SerializedName("effort") val effort: Int,
    @SerializedName("stat") val stat: StatResponse
)

data class StatResponse(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)


// Lista de los pokemon con la paginación para optimizar
data class PokemonListResponse(
    // Aqui no pongo lo de serializedName porque se llaman igual en la llamada que la variable
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String
)
