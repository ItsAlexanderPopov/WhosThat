package com.example.whosthat.pokemon;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whosthat.GameRepository;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonViewModel extends ViewModel {

    private final MutableLiveData<Integer> streakCounter = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final PokeApiService pokeApiService;
    private final GameRepository gameRepository;
    private final Random random = new Random();
    private Call<PokemonModel> pendingCall;

    public PokemonViewModel(PokeApiService pokeApiService) {
        this.pokeApiService = pokeApiService;
        this.gameRepository = GameRepository.getInstance();
    }

    public LiveData<String> getCurrentPokemonName() {
        return gameRepository.getCurrentPokemonName();
    }

    public LiveData<String> getCurrentSpriteUrl() {
        return gameRepository.getCurrentPokemonSpriteUrl();
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
        if (Boolean.TRUE.equals(isLoading.getValue())) {
            Log.d("PokemonViewModel", "Fetch already in progress, skipping");
            return;
        }
        Log.d("PokemonViewModel", "Fetching random Pokemon");
        isLoading.setValue(true);
        errorMessage.setValue(null);

        String previous = gameRepository.getCurrentPokemonName().getValue();
        int id = random.nextInt(PokeList.GEN1_COUNT) + 1;
        if (PokeList.isSamePokemon(PokeList.getPokemonByNumber(id), previous)) {
            id = id % PokeList.GEN1_COUNT + 1; // don't show the same Pokemon twice in a row
        }
        Log.d("PokemonViewModel", "Generated ID: " + id);
        pendingCall = pokeApiService.getPokemon(id);
        pendingCall.enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(Call<PokemonModel> call, Response<PokemonModel> response) {
                pendingCall = null;
                isLoading.setValue(false);
                PokemonModel pokemon = response.body();
                if (response.isSuccessful() && pokemon != null && pokemon.getImageUrl() != null) {
                    Log.d("PokemonViewModel", "Fetched Pokemon: " + pokemon.name);
                    gameRepository.setCurrentPokemon(pokemon.name, pokemon.getImageUrl());
                } else {
                    Log.e("PokemonViewModel", "Error fetching Pokemon data: " + response.code() + " " + response.message());
                    errorMessage.setValue("Error fetching Pokemon data: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {
                pendingCall = null;
                if (call.isCanceled()) {
                    return;
                }
                Log.e("PokemonViewModel", "Network error while fetching Pokemon", t);
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public boolean checkGuess(String guess) {
        String currentPokemon = gameRepository.getCurrentPokemonName().getValue();

        if (PokeList.isSamePokemon(guess, currentPokemon)) {
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

    public void resetStreak() {
        streakCounter.setValue(0);
    }

    @Override
    protected void onCleared() {
        if (pendingCall != null) {
            pendingCall.cancel();
        }
    }
}
