package com.lucas.trabajoapi

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
            // Al click de un Pokémon, cargar detalles
            fetchPokemonDetails(item.name)
        }

        binding.rvPokemon.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPokemon.adapter = adapter

        // Scroll listener para paginación
        binding.rvPokemon.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager as LinearLayoutManager
                val lastVisible = lm.findLastVisibleItemPosition()
                val totalCount = lm.itemCount

                if (!isLoading && lastVisible >= totalCount - 3) {
                    loadPokemon()
                }
            }
        })

        // Búsqueda
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotBlank()) fetchPokemonDetails(it.lowercase())
                }
                binding.searchView.clearFocus() // oculta teclado
                return true // <- importante para que no abra Google
            }
            override fun onQueryTextChange(newText: String?) = false
        })
    }

    private fun loadPokemon() {
        isLoading = true
        binding.progressBar.isVisible = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofit.create(ApiService::class.java)
                    .getPokemonList(offset = offset)

                if (response.isSuccessful) {
                    val list = response.body()?.results ?: emptyList()
                    offset += list.size

                    runOnUiThread {
                        adapter.addPokemon(list)
                        binding.progressBar.isVisible = false
                        isLoading = false
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Error cargando Pokémon", Toast.LENGTH_SHORT).show()
                        binding.progressBar.isVisible = false
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible = false
                    isLoading = false
                }
            }
        }
    }

    private fun fetchPokemonDetails(name: String) {
        binding.progressBar.isVisible = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofit.create(ApiService::class.java)
                    .getPokemonDetails(name)

                if (response.isSuccessful && response.body() != null) {
                    val pokemon = response.body()!!
                    runOnUiThread {
                        adapter.pokemonList.clear()
                        adapter.pokemonList.add(PokemonListItem(pokemon.species.name, ""))
                        adapter.notifyDataSetChanged()
                        binding.progressBar.isVisible = false
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Pokémon no encontrado", Toast.LENGTH_SHORT).show()
                        binding.progressBar.isVisible = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible = false
                }
            }
        }
    }
}
