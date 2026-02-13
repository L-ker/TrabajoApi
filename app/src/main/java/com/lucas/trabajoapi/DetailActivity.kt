package com.lucas.trabajoapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lucas.trabajoapi.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("pokemon_name") ?: return

        retrofit = Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 🔥 BOTÓN VOLVER
        binding.btnBack.setOnClickListener {
            finish()
        }

        loadPokemonDetails(name)
    }

    private fun loadPokemonDetails(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.create(ApiService::class.java)
                .getPokemonDetails(name)

            if (response.isSuccessful) {
                response.body()?.let { pokemon ->
                    runOnUiThread {
                        binding.tvName.text =
                            pokemon.species.name.replaceFirstChar { it.uppercase() }

                        val types = pokemon.types.joinToString(" / ") {
                            it.type.name.replaceFirstChar { c -> c.uppercase() }
                        }
                        binding.tvTypes.text = types

                        val abilities = pokemon.abilities.joinToString(", ") {
                            it.ability.name.replaceFirstChar { c -> c.uppercase() }
                        }
                        binding.tvAbilities.text = abilities

                        val stats = pokemon.stats.joinToString("\n") {
                            "${it.stat.name.replaceFirstChar { c -> c.uppercase() }}: ${it.baseStat}"
                        }
                        binding.tvStats.text = stats

                        Picasso.get()
                            .load(pokemon.sprites.frontDefault)
                            .into(binding.ivPokemon)
                    }
                }
            }
        }
    }
}
