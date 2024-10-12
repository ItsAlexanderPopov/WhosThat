package com.example.whosthat;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeagueOfLegendsViewModel extends ViewModel {
    private static final String TAG = "LeagueViewModel";
    private static final int INITIAL_BLUR_RADIUS = 25;
    private static final int BLUR_REDUCTION_STEP = 5;
    private static final int MIN_BLUR_RADIUS = 1;

    private final MutableLiveData<String> currentChampionName = new MutableLiveData<>();
    private final MutableLiveData<String> currentSplashArtUrl = new MutableLiveData<>();
    private final MutableLiveData<Integer> streakCounter = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentBlurRadius = new MutableLiveData<>(INITIAL_BLUR_RADIUS);
    private final MutableLiveData<Boolean> isChampionListLoaded = new MutableLiveData<>(false);

    private final LeagueApiService leagueApiService;
    private final Random random = new Random();

    public LeagueOfLegendsViewModel(LeagueApiService leagueApiService) {
        this.leagueApiService = leagueApiService;
    }

    public LiveData<String> getCurrentChampionName() { return currentChampionName; }
    public LiveData<String> getCurrentSplashArtUrl() { return currentSplashArtUrl; }
    public LiveData<Integer> getStreakCounter() { return streakCounter; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getCurrentBlurRadius() { return currentBlurRadius; }
    public LiveData<Boolean> getIsChampionListLoaded() { return isChampionListLoaded; }

    public void loadChampionList() {
        if (isChampionListLoaded.getValue() == Boolean.TRUE) {
            Log.d(TAG, "Champion list already loaded, skipping");
            return;
        }

        Log.d(TAG, "Loading champion list");
        isLoading.setValue(true);
        errorMessage.setValue(null);

        leagueApiService.getChampionList().enqueue(new Callback<LeagueChampionModel.ChampionList>() {
            @Override
            public void onResponse(Call<LeagueChampionModel.ChampionList> call, Response<LeagueChampionModel.ChampionList> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, LeagueChampionModel.ChampionData> champions = response.body().getChampions();
                    if (champions != null) {
                        Log.d(TAG, "Champion list loaded successfully. Champion count: " + champions.size());
                        ChampionList.initialize(champions);
                        isChampionListLoaded.setValue(true);
                    } else {
                        Log.e(TAG, "Champion data is null");
                        errorMessage.setValue("Champion data is null");
                    }
                } else {
                    Log.e(TAG, "Error loading champion list. Response code: " + response.code());
                    errorMessage.setValue("Error loading champion list: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LeagueChampionModel.ChampionList> call, Throwable t) {
                Log.e(TAG, "Network error while loading champion list", t);
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void fetchRandomChampion() {
        if (!ChampionList.isInitialized()) {
            Log.e(TAG, "Champion list not initialized. Cannot fetch random champion.");
            errorMessage.setValue("Champion list not initialized");
            return;
        }

        List<String> championNames = ChampionList.getChampionNames();
        if (championNames.isEmpty()) {
            Log.e(TAG, "Champion list is empty");
            errorMessage.setValue("Champion list is empty");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        String randomChampion = championNames.get(random.nextInt(championNames.size()));
        Log.d(TAG, "Fetching random champion: " + randomChampion);
        currentChampionName.setValue(randomChampion);

        fetchChampionData(randomChampion);
    }

    private void fetchChampionData(String championName) {
        Log.d(TAG, "Fetching data for champion: " + championName);
        String encodedChampionName = encodeChampionName(championName);
        Log.d(TAG, "Encoded champion name for API call: " + encodedChampionName);

        leagueApiService.getChampionData(encodedChampionName).enqueue(new Callback<LeagueChampionModel>() {
            @Override
            public void onResponse(Call<LeagueChampionModel> call, Response<LeagueChampionModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, LeagueChampionModel.ChampionData> championDataMap = response.body().getData();
                    if (championDataMap != null && !championDataMap.isEmpty()) {
                        LeagueChampionModel.ChampionData championData = championDataMap.values().iterator().next();
                        List<LeagueChampionModel.Skin> skins = championData.getSkins();
                        if (skins != null && !skins.isEmpty()) {
                            int randomSkinIndex = random.nextInt(skins.size());
                            String skinNum = String.valueOf(skins.get(randomSkinIndex).getNum());

                            String splashArtUrl = String.format("https://ddragon.leagueoflegends.com/cdn/img/champion/splash/%s_%s.jpg", encodedChampionName, skinNum);
                            Log.d(TAG, "Splash art URL: " + splashArtUrl);
                            currentSplashArtUrl.setValue(splashArtUrl);

                            currentBlurRadius.setValue(INITIAL_BLUR_RADIUS);
                        } else {
                            Log.e(TAG, "No skins found for champion: " + championName);
                            errorMessage.setValue("No skins found for champion: " + championName);
                        }
                    } else {
                        Log.e(TAG, "Champion data is empty for: " + championName);
                        errorMessage.setValue("Champion data is empty for: " + championName);
                    }
                } else {
                    Log.e(TAG, "Error fetching champion data. Response code: " + response.code());
                    errorMessage.setValue("Error fetching champion data: " + response.code());
                }
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<LeagueChampionModel> call, Throwable t) {
                Log.e(TAG, "Network error while fetching champion data", t);
                errorMessage.setValue("Network error: " + t.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    private String encodeChampionName(String championName) {
        Log.d(TAG, "Encoding champion name: " + championName);
        String encodedName = championName.replace(" ", "").replace("'", "");
        encodedName = encodedName.replace(".", "");
        encodedName = encodedName.replace("&", "");

        // Handle special cases
        switch (encodedName.toLowerCase()) {
            case "belveth":
                return "Belveth";
            case "chogath":
                return "Chogath";
            case "drmundo":
                return "DrMundo";
            case "jarvaniv":
                return "JarvanIV";
            case "kaisa":
                return "Kaisa";
            case "khazix":
                return "Khazix";
            case "kogmaw":
                return "KogMaw";
            case "leesin":
                return "LeeSin";
            case "nunuwillump":
                return "Nunu";
            case "reksai":
                return "RekSai";
            case "velkoz":
                return "Velkoz";
            case "wukong":
                return "MonkeyKing";
            case "renata glasc":
                return "Renata";
            default:
                return encodedName.substring(0, 1).toUpperCase() + encodedName.substring(1);
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

    private void increaseStreak() {
        Integer currentStreak = streakCounter.getValue();
        streakCounter.setValue(currentStreak != null ? currentStreak + 1 : 1);
    }

    public void resetStreak() {
        streakCounter.setValue(0);
    }
}