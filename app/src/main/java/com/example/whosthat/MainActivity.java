package com.example.whosthat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.whosthat.achievements.AchievementsActivity;
import com.example.whosthat.league.ChampionList;
import com.example.whosthat.league.LeagueChampionModel;
import com.example.whosthat.league.LeagueOfLegendsPage;
import com.example.whosthat.league.LeagueRetrofitClient;
import com.example.whosthat.pokemon.PokemonModel;
import com.example.whosthat.pokemon.PokemonPage;
import com.example.whosthat.pokemon.PokemonRetrofitClient;

import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean isDarkTheme;
    private ImageView themeToggleIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadThemePreference();
        applyTheme();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View pokemonButton = findViewById(R.id.button_pokemon);
        View championButton = findViewById(R.id.button_champion);
        View achievementsButton = findViewById(R.id.button_achievements);
        themeToggleIcon = findViewById(R.id.theme_toggle_icon);

        pokemonButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PokemonPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        championButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LeagueOfLegendsPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        achievementsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AchievementsActivity.class);
            startActivity(intent);
        });

        themeToggleIcon.setOnClickListener(v -> toggleTheme());

        updateThemeIcon();

        // Pre-fetch champion list
        preloadChampionList();
        preloadPokemon();
    }

    private void preloadPokemon() {
        if (GameRepository.getInstance().getCurrentPokemonName().getValue() != null) {
            return;
        }

        Log.d(TAG, "Preloading random Pokemon");
        int id = new Random().nextInt(151) + 1;
        PokemonRetrofitClient.getPokeApiService().getPokemon(id).enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(Call<PokemonModel> call, Response<PokemonModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonModel pokemon = response.body();
                    GameRepository.getInstance().setCurrentPokemonName(pokemon.name);
                    GameRepository.getInstance().setCurrentPokemonSpriteUrl(pokemon.sprites != null ? pokemon.sprites.frontDefault : null);
                    Log.d(TAG, "Pokemon preloaded: " + pokemon.name);
                }
            }

            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {
                Log.e(TAG, "Failed to preload Pokemon", t);
            }
        });
    }

    private void preloadChampionList() {
        if (ChampionList.isInitialized()) {
            return;
        }

        Log.d(TAG, "Preloading champion list");
        LeagueRetrofitClient.getLeagueApiService().getChampionList().enqueue(new Callback<LeagueChampionModel.ChampionList>() {
            @Override
            public void onResponse(Call<LeagueChampionModel.ChampionList> call, Response<LeagueChampionModel.ChampionList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, LeagueChampionModel.ChampionData> champions = response.body().getChampions();
                    if (champions != null) {
                        ChampionList.initialize(champions);
                        Log.d(TAG, "Champion list preloaded successfully");
                    }
                }
            }

            @Override
            public void onFailure(Call<LeagueChampionModel.ChampionList> call, Throwable t) {
                Log.e(TAG, "Failed to preload champion list", t);
            }
        });
    }

    private void loadThemePreference() {
        SharedPreferences prefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        isDarkTheme = prefs.getBoolean("isDarkTheme", true); // Default to dark theme
    }

    private void saveThemePreference() {
        SharedPreferences prefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isDarkTheme", isDarkTheme);
        editor.apply();
    }

    private void applyTheme() {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        saveThemePreference();
        applyTheme();
        updateThemeIcon();
        recreate(); // Recreate the activity to apply the new theme
    }

    private void updateThemeIcon() {
        if (isDarkTheme) {
            themeToggleIcon.setImageResource(R.drawable.ic_theme_toggle_light);
        } else {
            themeToggleIcon.setImageResource(R.drawable.ic_theme_toggle);
        }
    }
}