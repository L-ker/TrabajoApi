package com.lucas.trabajoapi

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lucas.trabajoapi.databinding.ItemPokemonBinding
import com.squareup.picasso.Picasso

// Representa un RecylcerView
class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemPokemonBinding.bind(view)

    fun bindListItem(item: PokemonListItem) {

        //Uppercase a la primera letra
        binding.tvPokemonName.text =
            item.name.replaceFirstChar { it.uppercase() }

        // Control de que la url es de la api
        val spriteUrl = if (item.url.contains("pokeapi.co")) {
            // Si es asi extrae el id  para hacer la url manualmente
            val id = item.url.trimEnd('/').takeLastWhile { it.isDigit() }
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
        } else {
            // Es URL directa del sprite cuando buscas
            item.url
        }

        // cargo la imagen desde internet con picasso
        Picasso.get()
            .load(spriteUrl)
            .into(binding.ivPokemon)

        binding.tvPokemonTypes.text = ""
    }
}
