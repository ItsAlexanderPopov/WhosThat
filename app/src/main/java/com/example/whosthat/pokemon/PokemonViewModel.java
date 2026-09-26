package com.example.whosthat.pokemon;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.whosthat.GameRepository;
import com.example.whosthat.game.Attribute;
import com.example.whosthat.game.GuessGameViewModel;
import com.example.whosthat.game.Hint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonViewModel extends GuessGameViewModel<PokemonProfile> {
    private static final String TAG = "PokemonViewModel";
    private static final int ATTRIBUTE_HINT_COST = 100;
    private static final int FIRST_LETTER_HINT_COST = 200;

    // Same columns as Pokedle's classic mode
    static final List<Attribute<PokemonProfile>> ATTRIBUTES = Collections.unmodifiableList(Arrays.asList(
            Attribute.<PokemonProfile>text("Type 1", p -> p.type1)
                    .partialIfIn(p -> p.type2 != null ? Collections.singletonList(p.type2) : null),
            Attribute.<PokemonProfile>text("Type 2", PokemonProfile::type2OrNone)
                    .partialIfIn(p -> Collections.singletonList(p.type1)),
            Attribute.<PokemonProfile>text("Habitat", p -> p.habitat),
            Attribute.<PokemonProfile>text("Color", p -> p.color),
            Attribute.<PokemonProfile>number("Stage", p -> (double) p.evolutionStage,
                    v -> String.format(Locale.US, "%d", v.intValue())),
            Attribute.<PokemonProfile>number("Height", p -> p.heightM,
                    v -> String.format(Locale.US, "%.1f m", v)),
            Attribute.<PokemonProfile>number("Weight", p -> p.weightKg,
                    v -> String.format(Locale.US, "%.1f kg", v))
    ));

    static final List<Hint<PokemonProfile>> HINTS;

    static {
        List<Hint<PokemonProfile>> hints = new ArrayList<>();
        for (Attribute<PokemonProfile> attribute : ATTRIBUTES) {
            hints.add(Hint.of(attribute, ATTRIBUTE_HINT_COST));
        }
        hints.add(new Hint<>("First letter", FIRST_LETTER_HINT_COST, p -> p.name.substring(0, 1)));
        HINTS = Collections.unmodifiableList(hints);
    }

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

    @Override
    protected List<Attribute<PokemonProfile>> attributes() {
        return ATTRIBUTES;
    }

    @Override
    protected List<Hint<PokemonProfile>> hints() {
        return HINTS;
    }

    @Override
    protected PokemonProfile findSubject(String input) {
        return PokedexData.byName(input);
    }

    @Override
    protected String nameOf(PokemonProfile subject) {
        return subject.name;
    }

    /** Called once the current Pokemon's image is on screen; starts its round (and clock). */
    public void onPokemonShown() {
        PokemonProfile target = PokedexData.byName(gameRepository.getCurrentPokemonName().getValue());
        if (target == null) {
            Log.e(TAG, "No Pokedex data for " + gameRepository.getCurrentPokemonName().getValue());
            return;
        }
        startRoundIfNew(target);
    }

    public void fetchRandomPokemon() {
        if (Boolean.TRUE.equals(isLoading.getValue())) {
            Log.d(TAG, "Fetch already in progress, skipping");
            return;
        }
        Log.d(TAG, "Fetching random Pokemon");
        isLoading.setValue(true);
        errorMessage.setValue(null);

        String previous = gameRepository.getCurrentPokemonName().getValue();
        int id = random.nextInt(PokeList.GEN1_COUNT) + 1;
        if (PokeList.isSamePokemon(PokeList.getPokemonByNumber(id), previous)) {
            id = id % PokeList.GEN1_COUNT + 1; // don't show the same Pokemon twice in a row
        }
        Log.d(TAG, "Generated ID: " + id);
        pendingCall = pokeApiService.getPokemon(id);
        pendingCall.enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(Call<PokemonModel> call, Response<PokemonModel> response) {
                pendingCall = null;
                isLoading.setValue(false);
                PokemonModel pokemon = response.body();
                if (response.isSuccessful() && pokemon != null && pokemon.getImageUrl() != null) {
                    Log.d(TAG, "Fetched Pokemon: " + pokemon.name);
                    gameRepository.setCurrentPokemon(pokemon.name, pokemon.getImageUrl());
                } else {
                    Log.e(TAG, "Error fetching Pokemon data: " + response.code() + " " + response.message());
                    errorMessage.setValue("Error fetching Pokemon data: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {
                pendingCall = null;
                if (call.isCanceled()) {
                    return;
                }
                Log.e(TAG, "Network error while fetching Pokemon", t);
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        if (pendingCall != null) {
            pendingCall.cancel();
        }
    }
}
