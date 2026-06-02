package com.example.whosthat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class GameRepository {
    private static GameRepository instance;

    private final MutableLiveData<String> currentPokemonName = new MutableLiveData<>();
    private final MutableLiveData<String> currentPokemonSpriteUrl = new MutableLiveData<>();

    private final MutableLiveData<String> currentChampionName = new MutableLiveData<>();
    private final MutableLiveData<String> currentChampionPortraitUrl = new MutableLiveData<>();

    private GameRepository() {}

    public static synchronized GameRepository getInstance() {
        if (instance == null) {
            instance = new GameRepository();
        }
        return instance;
    }

    public LiveData<String> getCurrentPokemonName() { return currentPokemonName; }
    public void setCurrentPokemonName(String name) { currentPokemonName.setValue(name); }

    public LiveData<String> getCurrentPokemonSpriteUrl() { return currentPokemonSpriteUrl; }
    public void setCurrentPokemonSpriteUrl(String url) { currentPokemonSpriteUrl.setValue(url); }

    public LiveData<String> getCurrentChampionName() { return currentChampionName; }
    public void setCurrentChampionName(String name) { currentChampionName.setValue(name); }

    public LiveData<String> getCurrentChampionPortraitUrl() { return currentChampionPortraitUrl; }
    public void setCurrentChampionPortraitUrl(String url) { currentChampionPortraitUrl.setValue(url); }
}