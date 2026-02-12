package com.lucas.trabajoapi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PokemonAdapter(
    var pokemonList: MutableList<PokemonListItem> = mutableListOf(),
    private val onItemClick: ((PokemonListItem) -> Unit)? = null
) : RecyclerView.Adapter<PokemonViewHolder>() {

    fun addPokemon(newList: List<PokemonListItem>) {
        val start = pokemonList.size
        pokemonList.addAll(newList)
        notifyItemRangeInserted(start, newList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val item = pokemonList[position]
        holder.bindListItem(item)
        holder.itemView.setOnClickListener { onItemClick?.invoke(item) }
    }

    override fun getItemCount() = pokemonList.size
}
