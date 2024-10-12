package com.example.whosthat.pokemon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonViewModel extends ViewModel {
    private static final int MAX_POKEMON_ID = 151;

    private final MutableLiveData<String> currentPokemonName = new MutableLiveData<>();
    private final MutableLiveData<String> currentSpriteUrl = new MutableLiveData<>();
    private final MutableLiveData<Integer> streakCounter = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final PokeApiService pokeApiService;

    public PokemonViewModel(PokeApiService pokeApiService) {
        this.pokeApiService = pokeApiService;
    }

    public LiveData<String> getCurrentPokemonName() {
        return currentPokemonName;
    }

    public LiveData<String> getCurrentSpriteUrl() {
        return currentSpriteUrl;
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

    public void fetchRandomPokemon() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        int id = new Random().nextInt(MAX_POKEMON_ID) + 1;
        pokeApiService.getPokemon(id).enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(Call<PokemonModel> call, Response<PokemonModel> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    PokemonModel pokemon = response.body();
                    currentPokemonName.setValue(pokemon.name);
                    currentSpriteUrl.setValue(pokemon.sprites != null ? pokemon.sprites.frontDefault : null);
                } else {
                    errorMessage.setValue("Error fetching Pokemon data: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public boolean checkGuess(String guess) {
        String normalizedGuess = PokeList.normalizePokemonName(guess);
        String currentPokemon = currentPokemonName.getValue();

        if (normalizedGuess.equals(currentPokemon)) {
            increaseStreak();
            return true;
        } else {
            resetStreak();
            return false;
        }
    }

    private void increaseStreak() {
        Integer currentStreak = streakCounter.getValue();
        streakCounter.setValue(currentStreak != null ? currentStreak + 1 : 1);
    }

    private void resetStreak() {
        streakCounter.setValue(0);
    }
}