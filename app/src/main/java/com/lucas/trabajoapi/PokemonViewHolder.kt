package com.lucas.trabajoapi

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lucas.trabajoapi.databinding.ItemPokemonBinding
import com.squareup.picasso.Picasso

class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemPokemonBinding.bind(view)

    fun bindDetails(pokemon: PokemonResponse) {
        binding.tvPokemonName.text = pokemon.species.name.capitalize()

        try {
            Picasso.get().load(pokemon.sprites.frontDefault).into(binding.ivPokemon)
        } catch (_: Exception) {}

        val typesText = pokemon.types
            .sortedBy { it.slot }
            .joinToString(" / ") { it.type.name.capitalize() }

        binding.tvPokemonTypes.text = typesText
    }

    fun bindListItem(item: PokemonListItem) {
        binding.tvPokemonName.text = item.name.capitalize()

        // Sacar ID de la URL
        val id = item.url.trimEnd('/').takeLastWhile { it.isDigit() }
        val spriteUrl =
            if (id.isNotEmpty()) "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
            else ""

        try {
            Picasso.get().load(spriteUrl).into(binding.ivPokemon)
        } catch (_: Exception) {}

        binding.tvPokemonTypes.text = "" // Por defecto vacío en la lista
    }
}
