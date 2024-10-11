package com.example.whosthat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeagueOfLegendsViewModel extends ViewModel {
    private static final int INITIAL_BLUR_RADIUS = 25;
    private static final int BLUR_REDUCTION_STEP = 5;
    private static final int MIN_BLUR_RADIUS = 1;

    private final MutableLiveData<String> currentChampionName = new MutableLiveData<>();
    private final MutableLiveData<String> currentSplashArtUrl = new MutableLiveData<>();
    private final MutableLiveData<Integer> streakCounter = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentBlurRadius = new MutableLiveData<>(INITIAL_BLUR_RADIUS);

    private final LeagueApiService leagueApiService;

    public LeagueOfLegendsViewModel(LeagueApiService leagueApiService) {
        this.leagueApiService = leagueApiService;
        if (!ChampionList.isInitialized()) {
            loadChampionList();
        }
    }

    public LiveData<String> getCurrentChampionName() {
        return currentChampionName;
    }

    public LiveData<String> getCurrentSplashArtUrl() {
        return currentSplashArtUrl;
    }

    public LiveData<Integer> getStreakCounter() {
        return streakCounter;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void loadChampionList() {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        leagueApiService.getChampionList().enqueue(new Callback<LeagueChampionModel.ChampionList>() {
            @Override
            public void onResponse(Call<LeagueChampionModel.ChampionList> call, Response<LeagueChampionModel.ChampionList> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ChampionList.initialize(response.body().champions);
                    fetchRandomChampion();
                } else {
                    errorMessage.setValue("Error loading champion list: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LeagueChampionModel.ChampionList> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchRandomChampion() {
        if (!ChampionList.isInitialized()) {
            loadChampionList();
            return;
        }

        List<String> championNames = ChampionList.getChampionNames();
        if (championNames.isEmpty()) {
            errorMessage.setValue("Champion list is empty");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        String randomChampion = championNames.get(new Random().nextInt(championNames.size()));
        currentChampionName.setValue(randomChampion);

        String encodedChampionName = encodeChampionName(randomChampion);
        currentSplashArtUrl.setValue("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/" + encodedChampionName + "_0.jpg");

        currentBlurRadius.setValue(INITIAL_BLUR_RADIUS);
        isLoading.setValue(false);
    }

    private String encodeChampionName(String championName) {
        // Remove spaces and apostrophes
        String encodedName = championName.replace(" ", "").replace("'", "");

        // Handle special cases
        encodedName = encodedName.replace(".", "");  // For champions like Dr.Mundo
        encodedName = encodedName.replace("&", "");  // For champions like Nunu&Willump

        // URL encode the result
        try {
            return URLEncoder.encode(encodedName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return encodedName;  // Return unencoded name if encoding fails
        }
    }


    public boolean checkGuess(String guess) {
        String currentChampion = currentChampionName.getValue();
        if (currentChampion == null) {
            return false;
        }

        if (guess.equalsIgnoreCase(currentChampion)) {
            increaseStreak();
            return true;
        } else {
            resetStreak();
            return false;
        }
    }

    public void reduceBlurRadius() {
        int currentRadius = currentBlurRadius.getValue() != null ? currentBlurRadius.getValue() : INITIAL_BLUR_RADIUS;
        int newRadius = Math.max(MIN_BLUR_RADIUS, currentRadius - BLUR_REDUCTION_STEP);
        currentBlurRadius.setValue(newRadius);
    }

    public LiveData<Integer> getCurrentBlurRadius() {
        return currentBlurRadius;
    }

    private void increaseStreak() {
        Integer currentStreak = streakCounter.getValue();
        streakCounter.setValue(currentStreak != null ? currentStreak + 1 : 1);
    }

    public void resetStreak() {
        streakCounter.setValue(0);
    }
}