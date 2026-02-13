package com.lucas.trabajoapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucas.trabajoapi.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: PokemonAdapter

    private var offset = 0
    private var isLoading = false
    private var isSearching = false   // 🔥 NUEVO
    private val pageSize = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        initUI()
        loadPokemon()
    }

    private fun initUI() {
        adapter = PokemonAdapter { item ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("pokemon_name", item.name)
            startActivity(intent)
        }

        binding.rvPokemon.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPokemon.adapter = adapter

        binding.rvPokemon.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager as LinearLayoutManager
                val lastVisible = lm.findLastVisibleItemPosition()
                val totalCount = lm.itemCount

                if (!isLoading && !isSearching && lastVisible >= totalCount - 3) {
                    loadPokemon()
                }
            }
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { fetchPokemonDetails(it.lowercase()) }
                return false
            }
            override fun onQueryTextChange(newText: String?) = false
        })

        binding.btnResetSearch.setOnClickListener {

            // 1️⃣ Salimos del modo búsqueda
            isSearching = false

            // 2️⃣ Evitamos dobles cargas
            isLoading = false

            // 3️⃣ Limpiamos UI
            binding.searchView.setQuery("", false)

            // 4️⃣ Limpiamos lista de forma segura
            adapter.pokemonList.clear()
            adapter.notifyDataSetChanged()

            // 5️⃣ Reiniciamos paginación
            offset = 0

            // 6️⃣ Cargamos desde cero
            loadPokemon()
        }
    }

    private fun loadPokemon() {
        isLoading = true
        binding.progressBar.isVisible = true

        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.create(ApiService::class.java)
                .getPokemonList(offset = offset, limit = pageSize)

            if (response.isSuccessful) {
                val list = response.body()?.results ?: emptyList()
                offset += list.size

                runOnUiThread {
                    adapter.addPokemon(list)
                    binding.progressBar.isVisible = false
                    isLoading = false
                }
            }
        }
    }

    private fun fetchPokemonDetails(name: String) {
        isSearching = true
        binding.progressBar.isVisible = true

        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.create(ApiService::class.java)
                .getPokemonDetails(name)

            if (response.isSuccessful) {
                response.body()?.let { pokemon ->
                    val spriteUrl = pokemon.sprites.frontDefault

                    runOnUiThread {
                        adapter.pokemonList.clear()
                        adapter.pokemonList.add(
                            PokemonListItem(
                                name = pokemon.species.name,
                                url = spriteUrl
                            )
                        )
                        adapter.notifyDataSetChanged()
                        binding.progressBar.isVisible = false
                    }
                }
            }
        }
    }
}
