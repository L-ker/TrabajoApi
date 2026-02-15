package com.lucas.trabajoapi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PokemonAdapter(
    // lista mutable con los pokemons
    var pokemonList: MutableList<PokemonListItem> = mutableListOf(),
    private val onItemClick: ((PokemonListItem) -> Unit)? = null
) : RecyclerView.Adapter<PokemonViewHolder>() {

    // guarda la posicion desde la que se añadiran los siguientes pokemon
    fun addPokemon(newList: List<PokemonListItem>) {
        val start = pokemonList.size
        pokemonList.addAll(newList)
        notifyItemRangeInserted(start, newList.size)
    }

    // reemplaza la lista
    fun setPokemon(newList: List<PokemonListItem>) {
        pokemonList.clear()
        pokemonList.addAll(newList)
        notifyDataSetChanged()
    }

    //pasa los datos al layout del item pokemon
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    // Muestra los datos del Pokémon en la vista y detecta el click del usuario
    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val item = pokemonList[position]
        holder.bindListItem(item)  // siempre bindListItem, porque ahora url viene bien
        holder.itemView.setOnClickListener { onItemClick?.invoke(item) }
    }

    // devuelve el tamaño de la lista
    override fun getItemCount() = pokemonList.size
}
