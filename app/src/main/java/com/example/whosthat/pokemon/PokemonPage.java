package com.example.whosthat.pokemon;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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
import com.example.whosthat.GameMode;
import com.example.whosthat.HighScoreManager;
import com.example.whosthat.MainActivity;
import com.example.whosthat.R;
import com.example.whosthat.achievements.AchievementNotifier;
import com.example.whosthat.game.GameBoardUi;
import com.example.whosthat.game.GuessOutcome;
import com.example.whosthat.game.Round;
import com.example.whosthat.game.Scoring;

import java.util.List;

public class PokemonPage extends AppCompatActivity {
    private static final int REVEAL_DURATION = 3000;
    private static final int TIMER_TICK_MS = 500;

    private PokemonViewModel viewModel;
    private ImageView imagePokemon;
    private AutoCompleteTextView inputPokemon;
    private Button buttonConfirmPokemon;
    private ProgressBar loadingIndicator;
    private NestedScrollView contentContainer;
    private Handler handler;
    private HighScoreManager highScoreManager;
    private GameBoardUi<PokemonProfile> gameUi;
    // True while the answer is shown, before the next Pokemon is requested
    private boolean revealPending = false;

    private final Runnable timerTick = new Runnable() {
        @Override
        public void run() {
            gameUi.updateTimer(viewModel.currentElapsedMs(), viewModel.currentPotential());
            handler.postDelayed(this, TIMER_TICK_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        highScoreManager = new HighScoreManager(this);
        handler = new Handler(Looper.getMainLooper());

        viewModel = new ViewModelProvider(this, new PokemonViewModelFactory(PokemonRetrofitClient.getPokeApiService()))
                .get(PokemonViewModel.class);

        initializeViews();
        setupToolbar();
        setupAutocomplete();
        setupObservers();
        setupBackNavigation();

        // Recreated (e.g. rotated) while the previous answer was being shown: move on to the next one
        Round<PokemonProfile> round = viewModel.getRound().getValue();
        if (savedInstanceState != null && round != null && !round.isPlaying()) {
            viewModel.fetchRandomPokemon();
        }
        // Otherwise the first Pokemon is fetched in onResume if the main menu hasn't preloaded one
    }

    private void initializeViews() {
        imagePokemon = findViewById(R.id.image_pokemon);
        inputPokemon = findViewById(R.id.input_pokemon);
        buttonConfirmPokemon = findViewById(R.id.button_confirm_pokemon);
        loadingIndicator = findViewById(R.id.loading_indicator);
        contentContainer = findViewById(R.id.content_container);
        gameUi = new GameBoardUi<>(this, this::onHintClicked);

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pokemonList);
        inputPokemon.setAdapter(adapter);
        inputPokemon.setThreshold(1);
    }

    private void setupObservers() {
        viewModel.getCurrentSpriteUrl().observe(this, this::loadImage);
        viewModel.getStreakCounter().observe(this, gameUi::setStreak);
        viewModel.getScore().observe(this, score -> gameUi.setScore(score, highScoreManager.getBestScore(GameMode.POKEMON)));
        viewModel.getRound().observe(this, gameUi::bindRound);
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
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                            Toast.makeText(PokemonPage.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            // The clues still work without the picture
                            viewModel.onPokemonShown();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                            applyColorFilter();
                            viewModel.onPokemonShown();
                            return false;
                        }
                    })
                    .into(imagePokemon);
        }
    }

    private void confirmPokemon() {
        Round<PokemonProfile> round = viewModel.getRound().getValue();
        if (round == null || !round.isPlaying()) {
            // Loading the next Pokemon failed earlier; treat the button as a retry
            if (!revealPending) {
                viewModel.fetchRandomPokemon();
            }
            return;
        }

        String enteredName = inputPokemon.getText().toString().trim();
        // delete this in production
        if (enteredName.equalsIgnoreCase("next")) {
            GuessOutcome outcome = viewModel.skip();
            if (outcome.type == GuessOutcome.Type.LOST) {
                AchievementNotifier.recordAndNotify(this, highScoreManager, highScoreManager::unlockSecretAchievement);
                Toast.makeText(this, "Skipped! It was " + outcome.answer + ".", Toast.LENGTH_SHORT).show();
                revealPokemon();
            }
            return;
        }

        GuessOutcome outcome = viewModel.submitGuess(enteredName);
        switch (outcome.type) {
            case INVALID:
                Toast.makeText(this, "Not a Gen-1 Pokemon name", Toast.LENGTH_SHORT).show();
                break;
            case DUPLICATE:
                Toast.makeText(this, "You already guessed that one", Toast.LENGTH_SHORT).show();
                break;
            case WRONG:
                Toast.makeText(this, "Wrong! -" + Scoring.WRONG_GUESS_PENALTY + " pts, "
                        + outcome.guessesLeft + " guesses left", Toast.LENGTH_SHORT).show();
                inputPokemon.setText("");
                break;
            case WON:
                Toast.makeText(this, "Correct! It's " + outcome.answer + "!\n" + outcome.points.describe(),
                        Toast.LENGTH_LONG).show();
                Integer streak = viewModel.getStreakCounter().getValue();
                AchievementNotifier.recordAndNotify(this, highScoreManager, () -> highScoreManager.recordRoundWon(
                        GameMode.POKEMON, streak != null ? streak : 0, outcome.runScore, outcome.points));
                revealPokemon();
                break;
            case LOST:
                Toast.makeText(this, "Out of guesses! It was " + outcome.answer + ".", Toast.LENGTH_LONG).show();
                revealPokemon();
                break;
            case NOT_READY:
                break;
        }
    }

    private void onHintClicked(int index) {
        String value = viewModel.revealHint(index);
        if (value == null) {
            return;
        }
        Round<PokemonProfile> round = viewModel.getRound().getValue();
        if (round != null) {
            int cost = round.getHints().get(index).getCost();
            Toast.makeText(this, "-" + cost + " pts", Toast.LENGTH_SHORT).show();
        }
    }

    private void revealPokemon() {
        revealPending = true;
        imagePokemon.setColorFilter(null);
        buttonConfirmPokemon.setEnabled(false);
        inputPokemon.setEnabled(false);

        handler.postDelayed(() -> {
            revealPending = false;
            buttonConfirmPokemon.setEnabled(true);
            inputPokemon.setEnabled(true);
            inputPokemon.setText("");
            viewModel.fetchRandomPokemon();
        }, REVEAL_DURATION);
    }

    private void updateLoadingState(boolean isLoading) {
        // Only hide the game for the very first load; later loads keep the board visible
        boolean firstLoad = isLoading && viewModel.getCurrentSpriteUrl().getValue() == null;
        loadingIndicator.setVisibility(firstLoad ? View.VISIBLE : View.GONE);
        contentContainer.setVisibility(firstLoad ? View.GONE : View.VISIBLE);
        buttonConfirmPokemon.setEnabled(!isLoading && !revealPending);
        inputPokemon.setEnabled(!isLoading && !revealPending);
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
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.resumeClock();
        handler.post(timerTick);
        if (viewModel.getCurrentSpriteUrl().getValue() == null) {
            viewModel.fetchRandomPokemon();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.pauseClock();
        handler.removeCallbacks(timerTick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
