package com.example.whosthat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.whosthat.achievements.AchievementsActivity;
import com.example.whosthat.league.LeagueOfLegendsPage;
import com.example.whosthat.pokemon.PokemonPage;

public class MainActivity extends AppCompatActivity {
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

        Button pokemonButton = findViewById(R.id.button_pokemon);
        Button championButton = findViewById(R.id.button_champion);
        Button achievementsButton = findViewById(R.id.button_achievements);
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