package com.example.whosthat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class PokemonPage extends AppCompatActivity {
    private static final String TAG = "PokemonPage";
    private static final int REVEAL_DURATION = 3000; // 3 seconds

    private PokemonViewModel viewModel;
    private ImageView imagePokemon;
    private AutoCompleteTextView inputPokemon;
    private Button buttonConfirmPokemon;
    private ProgressBar loadingIndicator;
    private NestedScrollView contentContainer;
    private TextView streakCounterTextView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        viewModel = new ViewModelProvider(this, new PokemonViewModelFactory(RetrofitClient.getPokeApiService()))
                .get(PokemonViewModel.class);

        initializeViews();
        setupToolbar();
        setupAutocomplete();
        setupObservers();
        setupBackNavigation();

        handler = new Handler(Looper.getMainLooper());

        if (savedInstanceState == null) {
            viewModel.fetchRandomPokemon();
        }
    }

    private void initializeViews() {
        imagePokemon = findViewById(R.id.image_pokemon);
        inputPokemon = findViewById(R.id.input_pokemon);
        buttonConfirmPokemon = findViewById(R.id.button_confirm_pokemon);
        loadingIndicator = findViewById(R.id.loading_indicator);
        contentContainer = findViewById(R.id.content_container);
        streakCounterTextView = findViewById(R.id.streak_counter);

        buttonConfirmPokemon.setOnClickListener(v -> confirmPokemon());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupAutocomplete() {
        List<String> pokemonList = PokeList.getFormattedPokemonList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, pokemonList);
        inputPokemon.setAdapter(adapter);
        inputPokemon.setThreshold(1);
    }

    private void setupObservers() {
        viewModel.getCurrentSpriteUrl().observe(this, this::loadImage);
        viewModel.getStreakCounter().observe(this, this::updateStreakCounter);
        viewModel.getIsLoading().observe(this, this::updateLoadingState);
        viewModel.getErrorMessage().observe(this, this::showError);
    }

    private void setupBackNavigation() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToMainActivity();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void loadImage(@Nullable String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Image load failed", e);
                            Toast.makeText(PokemonPage.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Image loaded successfully");
                            applyColorFilter();
                            return false;
                        }
                    })
                    .into(imagePokemon);
        }
    }

    private void confirmPokemon() {
        String enteredName = inputPokemon.getText().toString().trim();

        if (!PokeList.isGen1Pokemon(enteredName)) {
            Toast.makeText(this, "Not Gen-1 Pokemon Name", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isCorrect = viewModel.checkGuess(enteredName);
        if (isCorrect) {
            String displayName = PokeList.denormalizePokemonName(viewModel.getCurrentPokemonName().getValue());
            Toast.makeText(this, "Correct! It's " + displayName + "!", Toast.LENGTH_SHORT).show();
            revealPokemon();
        } else {
            Toast.makeText(this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void revealPokemon() {
        imagePokemon.setColorFilter(null);
        buttonConfirmPokemon.setEnabled(false);
        inputPokemon.setEnabled(false);

        handler.postDelayed(() -> {
            viewModel.fetchRandomPokemon();
            buttonConfirmPokemon.setEnabled(true);
            inputPokemon.setEnabled(true);
            inputPokemon.setText("");
        }, REVEAL_DURATION);
    }

    private void updateStreakCounter(int streak) {
        streakCounterTextView.setText("Streak: " + streak);
    }

    private void updateLoadingState(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        contentContainer.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void applyColorFilter() {
        int color = Color.parseColor("#FF000000");
        imagePokemon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navigateToMainActivity();
        return true;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        // Don't finish this activity
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called - Current Pokemon: " + viewModel.getCurrentPokemonName().getValue());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}