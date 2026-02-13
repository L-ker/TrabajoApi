package com.lucas.trabajoapi

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lucas.trabajoapi.databinding.ItemPokemonBinding
import com.squareup.picasso.Picasso

class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemPokemonBinding.bind(view)

    fun bindListItem(item: PokemonListItem) {

        binding.tvPokemonName.text =
            item.name.replaceFirstChar { it.uppercase() }

        val spriteUrl = if (item.url.contains("pokeapi.co")) {
            // Es URL de la API → sacar ID
            val id = item.url.trimEnd('/').takeLastWhile { it.isDigit() }
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
        } else {
            // Es URL directa del sprite (cuando buscas)
            item.url
        }

        Picasso.get()
            .load(spriteUrl)
            .into(binding.ivPokemon)

        binding.tvPokemonTypes.text = ""
    }
}
