package com.example.whosthat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonPage extends AppCompatActivity {

    private static final String TAG = "PokemonPage";
    private static final int REVEAL_DURATION = 3000; // 3 seconds
    private ImageView imagePokemon;
    private AutoCompleteTextView inputPokemon;
    private Button buttonConfirmPokemon;
    private ProgressBar loadingIndicator;
    private NestedScrollView contentContainer;
    private TextView streakCounterTextView;
    private Random random;
    private String currentPokemonName;
    private String currentSpriteUrl;
    private PokeApiService pokeApiService;
    private int streakCounter = 0;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        setupToolbar();
        initializeViews();
        setupAutocomplete();
        setupButtonListener();

        pokeApiService = RetrofitClient.getPokeApiService();
        handler = new Handler(Looper.getMainLooper());

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            fetchRandomPokemon();
        }
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
        loadingIndicator = findViewById(R.id.loading_indicator);
        contentContainer = findViewById(R.id.content_container);
        streakCounterTextView = findViewById(R.id.streak_counter);
        random = new Random();
        updateStreakCounter();
    }

    private void setupAutocomplete() {
        List<String> pokemonList = PokeList.getFormattedPokemonList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, pokemonList);
        inputPokemon.setAdapter(adapter);
        inputPokemon.setThreshold(1);
    }

    private void setupButtonListener() {
        buttonConfirmPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPokemon();
            }
        });
    }

    private void fetchRandomPokemon() {
        showLoading();
        int id = random.nextInt(151) + 1;
        Log.d(TAG, "Fetching Pokemon with ID: " + id);

        pokeApiService.getPokemon(id).enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(Call<PokemonModel> call, Response<PokemonModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonModel pokemon = response.body();

                    currentPokemonName = pokemon.name;
                    currentSpriteUrl = pokemon.sprites != null ? pokemon.sprites.frontDefault : null;

                    Log.d(TAG, "Pokemon name: " + currentPokemonName);
                    Log.d(TAG, "Sprite URL: " + currentSpriteUrl);

                    if (currentSpriteUrl != null && !currentSpriteUrl.isEmpty()) {
                        loadImage(currentSpriteUrl);
                    } else {
                        Log.e(TAG, "Sprite URL is null or empty");
                        Toast.makeText(PokemonPage.this, "Error: Sprite URL is missing", Toast.LENGTH_SHORT).show();
                        showContent();
                    }

                    inputPokemon.setText("");
                } else {
                    Log.e(TAG, "Error response: " + response.code() + " " + response.message());
                    Toast.makeText(PokemonPage.this, "Error fetching Pokemon data: " + response.code(), Toast.LENGTH_SHORT).show();
                    showContent();
                }
            }

            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                Toast.makeText(PokemonPage.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showContent();
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
                        showContent();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Image loaded successfully");
                        applyColorFilter();
                        showContent();
                        return false;
                    }
                })
                .into(imagePokemon);
    }

    private void applyColorFilter() {
        int color = Color.parseColor("#FFF0E6D2");
        imagePokemon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void showLoading() {
        loadingIndicator.setVisibility(View.VISIBLE);
        contentContainer.setVisibility(View.GONE);
    }

    private void showContent() {
        loadingIndicator.setVisibility(View.GONE);
        contentContainer.setVisibility(View.VISIBLE);
    }

    private void confirmPokemon() {
        String enteredName = inputPokemon.getText().toString().trim();
        String normalizedInput = PokeList.normalizePokemonName(enteredName);

        Log.d(TAG, "Entered name: " + normalizedInput);
        Log.d(TAG, "Current Pokemon name: " + currentPokemonName);

        if (!PokeList.isGen1Pokemon(enteredName)) {
            Toast.makeText(this, "Not Gen-1 Pokemon Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (normalizedInput.equals(currentPokemonName)) {
            streakCounter++;
            updateStreakCounter();
            String displayName = PokeList.denormalizePokemonName(currentPokemonName);
            Toast.makeText(this, "Correct! It's " + displayName + "!", Toast.LENGTH_SHORT).show();
            revealPokemon();
        } else {
            streakCounter = 0;
            updateStreakCounter();
            Toast.makeText(this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void revealPokemon() {
        imagePokemon.setColorFilter(null); // Remove color filter
        buttonConfirmPokemon.setEnabled(false); // Disable button during reveal
        inputPokemon.setEnabled(false); // Disable input during reveal

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchRandomPokemon(); // Fetch new Pokemon after reveal
                buttonConfirmPokemon.setEnabled(true);
                inputPokemon.setEnabled(true);
            }
        }, REVEAL_DURATION);
    }

    private void updateStreakCounter() {
        streakCounterTextView.setText("Streak: " + streakCounter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentPokemonName", currentPokemonName);
        outState.putString("currentSpriteUrl", currentSpriteUrl);
        outState.putInt("streakCounter", streakCounter);
    }

    private void restoreState(Bundle savedInstanceState) {
        currentPokemonName = savedInstanceState.getString("currentPokemonName");
        currentSpriteUrl = savedInstanceState.getString("currentSpriteUrl");
        streakCounter = savedInstanceState.getInt("streakCounter");
        updateStreakCounter();
        if (currentSpriteUrl != null && !currentSpriteUrl.isEmpty()) {
            showLoading();
            loadImage(currentSpriteUrl);
        } else {
            showContent();
        }
        Log.d(TAG, "State restored - Pokemon: " + currentPokemonName + ", Streak: " + streakCounter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called - Current Pokemon: " + currentPokemonName + ", Streak: " + streakCounter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}