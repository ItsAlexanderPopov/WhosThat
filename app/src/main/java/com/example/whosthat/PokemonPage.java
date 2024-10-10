package com.example.whosthat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonPage extends AppCompatActivity {

    private static final String TAG = "PokemonPage";
    private ImageView imagePokemon;
    private TextInputEditText inputPokemon;
    private Button buttonConfirmPokemon;
    private Random random;
    private String currentPokemonName;
    private PokeApiService pokeApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        setupToolbar();
        initializeViews();
        setupButtonListener();
        setupBackPressHandler();

        pokeApiService = RetrofitClient.getPokeApiService();

        fetchRandomPokemon();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initializeViews() {
        imagePokemon = findViewById(R.id.image_pokemon);
        inputPokemon = findViewById(R.id.input_pokemon);
        buttonConfirmPokemon = findViewById(R.id.button_confirm_pokemon);
        random = new Random();
    }

    private void setupButtonListener() {
        buttonConfirmPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPokemon();
            }
        });
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void fetchRandomPokemon() {
        int id = random.nextInt(151) + 1;
        Log.d(TAG, "Fetching Pokemon with ID: " + id);

        pokeApiService.getPokemon(id).enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(Call<PokemonModel> call, Response<PokemonModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonModel pokemon = response.body();

                    currentPokemonName = pokemon.name;
                    String spriteUrl = pokemon.sprites != null ? pokemon.sprites.frontDefault : null;

                    Log.d(TAG, "Pokemon name: " + currentPokemonName);
                    Log.d(TAG, "Sprite URL: " + spriteUrl);

                    if (spriteUrl != null && !spriteUrl.isEmpty()) {
                        loadImage(spriteUrl);
                    } else {
                        Log.e(TAG, "Sprite URL is null or empty");
                        Toast.makeText(PokemonPage.this, "Error: Sprite URL is missing", Toast.LENGTH_SHORT).show();
                    }

                    inputPokemon.setText("");
                } else {
                    Log.e(TAG, "Error response: " + response.code() + " " + response.message());
                    Toast.makeText(PokemonPage.this, "Error fetching Pokemon data: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                Toast.makeText(PokemonPage.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage(String url) {
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Image load failed", e);
                        Toast.makeText(PokemonPage.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Image loaded successfully");
                        return false;
                    }
                })
                .into(imagePokemon);
    }

    private void confirmPokemon() {
        String enteredName = inputPokemon.getText().toString().toLowerCase().trim();
        if (enteredName.equals(currentPokemonName)) {
            Toast.makeText(this, "Correct! It's " + currentPokemonName + "!", Toast.LENGTH_SHORT).show();
            fetchRandomPokemon(); // Fetch a new Pokemon
        } else {
            Toast.makeText(this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}