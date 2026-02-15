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
    // declaracion de variables

    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: PokemonAdapter

    private var offset = 0
    private var isLoading = false
    private var isSearching = false
    private val pageSize = 20


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // añadimos la url base y el conversor
        retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        initUI()
        loadPokemon()
    }

    private fun initUI() {

        // Iniciación adapter y definir que pasa al pulsar items
        adapter = PokemonAdapter { item ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("pokemon_name", item.name)
            startActivity(intent)
        }

        // Configura el RecyclerView con scroll horizontal
        binding.rvPokemon.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPokemon.adapter = adapter

        // implementando scroll infinito para cargar pokemons al final y optimizar todo
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

        // cuando el usuario busca algo
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { fetchPokemonDetails(it.lowercase()) }
                return false
            }
            override fun onQueryTextChange(newText: String?) = false
        })

        binding.btnResetSearch.setOnClickListener {

            // Para de buscar
            isSearching = false

            // Evitar errores de carga
            isLoading = false

            // limpiar interfaz
            binding.searchView.setQuery("", false)

            // Limpiar lista
            adapter.pokemonList.clear()
            adapter.notifyDataSetChanged()

            // Reiniciamos el offset que se usa para optimizar las llamadas
            offset = 0

            // Cargo pokemons
            loadPokemon()
        }
    }

    private fun loadPokemon() {
        isLoading = true
        binding.progressBar.isVisible = true

        // ejecuta la peticion fuera del hilo principal para no parar al usuario
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.create(ApiService::class.java)
                .getPokemonList(offset = offset, limit = pageSize)

            if (response.isSuccessful) {
                val list = response.body()?.results ?: emptyList()
                offset += list.size

                // actualiza el hilo del usuario
                runOnUiThread {
                    adapter.addPokemon(list)
                    binding.progressBar.isVisible = false
                    isLoading = false
                }
            }
        }
    }

    private fun fetchPokemonDetails(name: String) {
        // modo busqueda e indicador de que esta cargando del layout
        isSearching = true
        binding.progressBar.isVisible = true

        // preparando para la llamada sin bloquear hilo principal
        CoroutineScope(Dispatchers.IO).launch {
            //retrofit crea implementacion de apiService para usar el getpokemondetails con el anme
            val response = retrofit.create(ApiService::class.java)
                .getPokemonDetails(name)


            // si succesful
            if (response.isSuccessful) {
                //extraemos cuerpo para filtrar
                response.body()?.let { pokemon ->
                    val spriteUrl = pokemon.sprites.frontDefault

                    // modificando hilo principal
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
