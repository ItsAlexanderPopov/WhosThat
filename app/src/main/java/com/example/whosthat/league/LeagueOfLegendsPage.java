package com.example.whosthat.league;

import android.content.Intent;
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.whosthat.MainActivity;
import com.example.whosthat.R;
import com.example.whosthat.HighScoreManager;
import jp.wasabeef.glide.transformations.BlurTransformation;

import java.util.List;

public class LeagueOfLegendsPage extends AppCompatActivity {
    private static final int REVEAL_DURATION = 1000;
    private static final int BLUR_SAMPLING = 4;
    private static final int MAX_ATTEMPTS = 3;


    private LeagueOfLegendsViewModel viewModel;
    private ImageView imageChampion;
    private AutoCompleteTextView inputChampion;
    private Button buttonConfirmChampion;
    private ProgressBar loadingIndicator;
    private NestedScrollView contentContainer;
    private TextView streakCounterTextView;
    private TextView attemptsLeftTextView;
    private Handler handler;
    private HighScoreManager highScoreManager;
    private int currentAttempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leagueoflegends);
        highScoreManager = new HighScoreManager(this);

        viewModel = new ViewModelProvider(this, new LeagueViewModelFactory(LeagueRetrofitClient.getLeagueApiService()))
                .get(LeagueOfLegendsViewModel.class);

        initializeViews();
        setupToolbar();
        setupObservers();
        setupBackNavigation();

        handler = new Handler(Looper.getMainLooper());

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
        streakCounterTextView = findViewById(R.id.streak_counter);
        attemptsLeftTextView = findViewById(R.id.attempts_left);

        buttonConfirmChampion.setOnClickListener(v -> confirmChampion());
        updateAttemptsLeftText();
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
        viewModel.getCurrentSplashArtUrl().observe(this, this::loadImage);
        viewModel.getStreakCounter().observe(this, this::updateStreakCounter);
        viewModel.getIsLoading().observe(this, this::updateLoadingState);
        viewModel.getErrorMessage().observe(this, this::showError);
        viewModel.getCurrentChampionName().observe(this, name -> {
            if (name != null && !name.isEmpty()) {
                setupAutocomplete();
            }
        });
        viewModel.getCurrentBlurRadius().observe(this, blurRadius -> {
            loadImage(viewModel.getCurrentSplashArtUrl().getValue());
        });
        viewModel.getIsChampionListLoaded().observe(this, isLoaded -> {
            if (isLoaded) {
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
            Glide.with(this)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(currentBlurRadius, BLUR_SAMPLING)))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Toast.makeText(LeagueOfLegendsPage.this, "Failed to load image. Please try again.", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageChampion);
        }
    }

    private void confirmChampion() {
        String enteredName = inputChampion.getText().toString().trim();

        if (!ChampionList.isValidChampion(enteredName)) {
            Toast.makeText(this, "Not a valid champion name", Toast.LENGTH_SHORT).show();
            return;
        }

        currentAttempts++;
        boolean isCorrect = viewModel.checkGuess(enteredName);
        if (isCorrect) {
            Toast.makeText(this, "Correct! It's " + enteredName + "!", Toast.LENGTH_SHORT).show();
            viewModel.increaseStreak();
            int currentStreak = viewModel.getStreakCounter().getValue();
            highScoreManager.updateHighStreakLeagueOfLegends(currentStreak);
            revealChampion();
        } else {
            if (currentAttempts > MAX_ATTEMPTS) {
                String correctName = viewModel.getCurrentChampionName().getValue();
                Toast.makeText(this, "Wrong! It was " + correctName, Toast.LENGTH_LONG).show();
                viewModel.resetStreak();
                revealChampion();
            } else {
                Toast.makeText(this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
                viewModel.reduceBlurRadius();
                updateAttemptsLeftText();
            }
        }
    }

    private void revealChampion() {
        Glide.with(this)
                .load(viewModel.getCurrentSplashArtUrl().getValue())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageChampion);

        buttonConfirmChampion.setEnabled(false);
        inputChampion.setEnabled(false);

        handler.postDelayed(() -> {
            viewModel.fetchRandomChampion();
            buttonConfirmChampion.setEnabled(true);
            inputChampion.setEnabled(true);
            inputChampion.setText("");
            currentAttempts = 0;
            updateAttemptsLeftText();
        }, REVEAL_DURATION);
    }

    private void updateAttemptsLeftText() {
        attemptsLeftTextView.setText(String.valueOf(MAX_ATTEMPTS - currentAttempts));    }

    private void updateStreakCounter(int streak) {
        streakCounterTextView.setText(String.valueOf(streak));
    }

    private void updateLoadingState(boolean isLoading) {
        loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        contentContainer.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        buttonConfirmChampion.setEnabled(!isLoading);
        inputChampion.setEnabled(!isLoading);
    }

    private void showError(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            contentContainer.setVisibility(View.GONE);
        } else {
            contentContainer.setVisibility(View.VISIBLE);
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
        if (ChampionList.isInitialized()) {
            setupAutocomplete();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}