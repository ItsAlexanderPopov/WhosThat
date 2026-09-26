package com.example.whosthat.league;

import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
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

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

import java.util.List;

public class LeagueOfLegendsPage extends AppCompatActivity {
    private static final int REVEAL_DURATION = 2500;
    private static final int BLUR_SAMPLING = 3;
    private static final int TIMER_TICK_MS = 500;

    private LeagueOfLegendsViewModel viewModel;
    private ImageView imageChampion;
    private AutoCompleteTextView inputChampion;
    private Button buttonConfirmChampion;
    private ProgressBar loadingIndicator;
    private NestedScrollView contentContainer;
    private Handler handler;
    private HighScoreManager highScoreManager;
    private GameBoardUi<ChampionProfile> gameUi;
    // True while the answer is shown, before the next champion is picked
    private boolean revealPending = false;
    // What is currently drawn, so the URL and blur observers don't load the same image twice
    private String loadedUrl;
    private int loadedBlurRadius = -1;

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
        setContentView(R.layout.activity_leagueoflegends);
        highScoreManager = new HighScoreManager(this);
        handler = new Handler(Looper.getMainLooper());

        viewModel = new ViewModelProvider(this, new LeagueViewModelFactory(LeagueRetrofitClient.getLeagueApiService()))
                .get(LeagueOfLegendsViewModel.class);

        initializeViews();
        setupToolbar();
        setupObservers();
        setupBackNavigation();

        // Recreated (e.g. rotated) while the previous answer was being shown: move on to the next one
        Round<ChampionProfile> round = viewModel.getRound().getValue();
        if (savedInstanceState != null && round != null && !round.isPlaying()) {
            viewModel.fetchRandomChampion();
        }

        if (savedInstanceState == null) {
            viewModel.loadChampionList();
        }
    }

    private void initializeViews() {
        imageChampion = findViewById(R.id.image_champion);
        inputChampion = findViewById(R.id.input_champion);
        buttonConfirmChampion = findViewById(R.id.button_confirm_champion);
        loadingIndicator = findViewById(R.id.loading_indicator);
        contentContainer = findViewById(R.id.content_container);
        gameUi = new GameBoardUi<>(this, this::onHintClicked);

        buttonConfirmChampion.setOnClickListener(v -> confirmChampion());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupObservers() {
        viewModel.getCurrentChampionPortraitUrl().observe(this, this::loadImage);
        viewModel.getStreakCounter().observe(this, gameUi::setStreak);
        viewModel.getScore().observe(this, score -> gameUi.setScore(score, highScoreManager.getBestScore(GameMode.LEAGUE)));
        viewModel.getRound().observe(this, gameUi::bindRound);
        viewModel.getIsLoading().observe(this, this::updateLoadingState);
        viewModel.getErrorMessage().observe(this, this::showError);
        viewModel.getCurrentBlurRadius().observe(this, blurRadius -> {
            // Only re-blur the champion already on screen; new champions come through the URL observer
            String url = viewModel.getCurrentChampionPortraitUrl().getValue();
            if (url != null && url.equals(loadedUrl)) {
                loadImage(url);
            }
        });
        viewModel.getIsChampionListLoaded().observe(this, isLoaded -> {
            if (!isLoaded) {
                return;
            }
            setupAutocomplete();
            if (viewModel.getCurrentChampionName().getValue() == null) {
                viewModel.fetchRandomChampion();
            }
        });
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

    private void setupAutocomplete() {
        List<String> championNames = ChampionList.getChampionNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, championNames);
        inputChampion.setAdapter(adapter);
        inputChampion.setThreshold(1);
    }

    private void loadImage(String url) {
        if (url != null && !url.isEmpty()) {
            Integer currentBlurRadius = viewModel.getCurrentBlurRadius().getValue();
            if (currentBlurRadius == null) {
                currentBlurRadius = 1; // Fallback to minimum blur if null
            }
            if (url.equals(loadedUrl) && currentBlurRadius == loadedBlurRadius) {
                return;
            }
            loadedUrl = url;
            loadedBlurRadius = currentBlurRadius;

            MultiTransformation<Bitmap> multiTransformation = new MultiTransformation<>(
                    new CenterCrop(),
                    new BlurTransformation(currentBlurRadius, BLUR_SAMPLING),
                    new GrayscaleTransformation()
            );

            Glide.with(this)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(multiTransformation))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Toast.makeText(LeagueOfLegendsPage.this, "Failed to load image. Please try again.", Toast.LENGTH_SHORT).show();
                            // The clues still work without the picture
                            viewModel.onChampionShown();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            viewModel.onChampionShown();
                            return false;
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageChampion);
        }
    }

    private void confirmChampion() {
        Round<ChampionProfile> round = viewModel.getRound().getValue();
        if (round == null || !round.isPlaying()) {
            return;
        }

        String enteredName = inputChampion.getText().toString().trim();
        // delete this in production
        if (enteredName.equalsIgnoreCase("next")) {
            GuessOutcome outcome = viewModel.skip();
            if (outcome.type == GuessOutcome.Type.LOST) {
                AchievementNotifier.recordAndNotify(this, highScoreManager, highScoreManager::unlockSecretAchievement);
                Toast.makeText(this, "Skipped! It was " + outcome.answer + ".", Toast.LENGTH_SHORT).show();
                revealChampion();
            }
            return;
        }

        GuessOutcome outcome = viewModel.submitGuess(enteredName);
        switch (outcome.type) {
            case INVALID:
                Toast.makeText(this, "Not a valid champion name", Toast.LENGTH_SHORT).show();
                break;
            case DUPLICATE:
                Toast.makeText(this, "You already guessed that one", Toast.LENGTH_SHORT).show();
                break;
            case WRONG:
                Toast.makeText(this, "Wrong! -" + Scoring.WRONG_GUESS_PENALTY + " pts, "
                        + outcome.guessesLeft + " guesses left", Toast.LENGTH_SHORT).show();
                inputChampion.setText("");
                break;
            case WON:
                Toast.makeText(this, "Correct! It's " + outcome.answer + "!\n" + outcome.points.describe(),
                        Toast.LENGTH_LONG).show();
                Integer streak = viewModel.getStreakCounter().getValue();
                AchievementNotifier.recordAndNotify(this, highScoreManager, () -> highScoreManager.recordRoundWon(
                        GameMode.LEAGUE, streak != null ? streak : 0, outcome.runScore, outcome.points));
                revealChampion();
                break;
            case LOST:
                Toast.makeText(this, "Out of guesses! It was " + outcome.answer + ".", Toast.LENGTH_LONG).show();
                revealChampion();
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
        Round<ChampionProfile> round = viewModel.getRound().getValue();
        if (round != null) {
            int cost = round.getHints().get(index).getCost();
            Toast.makeText(this, "-" + cost + " pts", Toast.LENGTH_SHORT).show();
        }
    }

    private void revealChampion() {
        revealPending = true;
        loadedUrl = null;
        loadedBlurRadius = -1;
        Glide.with(this)
                .load(viewModel.getCurrentChampionPortraitUrl().getValue())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageChampion);

        buttonConfirmChampion.setEnabled(false);
        inputChampion.setEnabled(false);

        handler.postDelayed(() -> {
            revealPending = false;
            viewModel.fetchRandomChampion();
            buttonConfirmChampion.setEnabled(true);
            inputChampion.setEnabled(true);
            inputChampion.setText("");
        }, REVEAL_DURATION);
    }

    private void updateLoadingState(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        contentContainer.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        buttonConfirmChampion.setEnabled(!isLoading && !revealPending);
        inputChampion.setEnabled(!isLoading && !revealPending);
    }

    private void showError(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.resumeClock();
        handler.post(timerTick);

        // If champion list is not loaded or current champion is null (due to error), retry
        if (!ChampionList.isInitialized()) {
            viewModel.loadChampionList();
        } else if (viewModel.getCurrentChampionPortraitUrl().getValue() == null) {
            viewModel.fetchRandomChampion();
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
