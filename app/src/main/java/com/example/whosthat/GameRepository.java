package com.example.whosthat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class GameRepository {
    private static GameRepository instance;

    private final MutableLiveData<String> currentPokemonName = new MutableLiveData<>();
    private final MutableLiveData<String> currentPokemonSpriteUrl = new MutableLiveData<>();

    private boolean pokemonRequestInFlight = false;

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

    public LiveData<String> getCurrentPokemonSpriteUrl() { return currentPokemonSpriteUrl; }

    // Name first so the image observer never pairs a new picture with the previous answer
    public void setCurrentPokemon(String name, String spriteUrl) {
        currentPokemonName.setValue(name);
        currentPokemonSpriteUrl.setValue(spriteUrl);
    }

    public boolean isPokemonRequestInFlight() { return pokemonRequestInFlight; }
    public void setPokemonRequestInFlight(boolean inFlight) { pokemonRequestInFlight = inFlight; }

    public LiveData<String> getCurrentChampionName() { return currentChampionName; }
    public void setCurrentChampionName(String name) { currentChampionName.setValue(name); }

    public LiveData<String> getCurrentChampionPortraitUrl() { return currentChampionPortraitUrl; }
    public void setCurrentChampionPortraitUrl(String url) { currentChampionPortraitUrl.setValue(url); }
}