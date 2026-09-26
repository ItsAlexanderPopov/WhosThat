package com.example.whosthat.league;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whosthat.GameRepository;

import java.util.Random;

public class LeagueOfLegendsViewModel extends ViewModel {
    private static final String TAG = "LeagueViewModel";
    private static final int INITIAL_BLUR_RADIUS = 70;
    private static final int BLUR_REDUCTION_STEP = 15;
    private static final int MIN_BLUR_RADIUS = 1;

    private final MutableLiveData<Integer> streakCounter = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentBlurRadius = new MutableLiveData<>(INITIAL_BLUR_RADIUS);
    private final MutableLiveData<Boolean> isChampionListLoaded = new MutableLiveData<>(false);

    private final LeagueApiService leagueApiService;
    private final GameRepository gameRepository;
    private final Random random = new Random();

    public LeagueOfLegendsViewModel(LeagueApiService leagueApiService) {
        this.leagueApiService = leagueApiService;
        this.gameRepository = GameRepository.getInstance();
    }

    public LiveData<String> getCurrentChampionName() { return gameRepository.getCurrentChampionName(); }
    public LiveData<String> getCurrentChampionPortraitUrl() { return gameRepository.getCurrentChampionPortraitUrl(); }
    public LiveData<Integer> getStreakCounter() { return streakCounter; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getCurrentBlurRadius() { return currentBlurRadius; }
    public LiveData<Boolean> getIsChampionListLoaded() { return isChampionListLoaded; }

    public void loadChampionList() {
        if (ChampionList.isInitialized()) {
            isChampionListLoaded.setValue(true);
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);
        ChampionListLoader.load(leagueApiService, new ChampionListLoader.Listener() {
            @Override
            public void onLoaded() {
                isLoading.setValue(false);
                isChampionListLoaded.setValue(true);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void fetchRandomChampion() {
        LeagueChampionModel.ChampionData champion =
                ChampionList.getRandomChampion(random, gameRepository.getCurrentChampionName().getValue());
        if (champion == null) {
            Log.e(TAG, "Champion list not initialized. Cannot pick a random champion.");
            errorMessage.setValue("Champion list not loaded yet");
            return;
        }

        String portraitUrl = ChampionList.getPortraitUrl(champion);
        Log.d(TAG, "Picked " + champion.getName() + " -> " + portraitUrl);
        // Reset the blur before publishing the new image so it is never shown unblurred
        currentBlurRadius.setValue(INITIAL_BLUR_RADIUS);
        gameRepository.setCurrentChampionName(champion.getName());
        gameRepository.setCurrentChampionPortraitUrl(portraitUrl);
    }

    public boolean checkGuess(String guess) {
        String currentChampion = gameRepository.getCurrentChampionName().getValue();
        if (currentChampion == null) {
            return false;
        }

        LeagueChampionModel.ChampionData guessed = ChampionList.findChampion(guess);
        return guessed != null && guessed.getName().equals(currentChampion);
    }

    public void reduceBlurRadius() {
        int currentRadius = currentBlurRadius.getValue() != null ? currentBlurRadius.getValue() : INITIAL_BLUR_RADIUS;
        int newRadius = Math.max(MIN_BLUR_RADIUS, currentRadius - BLUR_REDUCTION_STEP);
        currentBlurRadius.setValue(newRadius);
    }

    public void increaseStreak() {
        Integer currentStreakValue = streakCounter.getValue();
        streakCounter.setValue(currentStreakValue != null ? currentStreakValue + 1 : 1);
    }

    public void resetStreak() {
        streakCounter.setValue(0);
    }
}