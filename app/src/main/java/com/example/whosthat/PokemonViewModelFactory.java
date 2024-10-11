package com.example.whosthat;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PokemonViewModelFactory implements ViewModelProvider.Factory {
    private final PokeApiService pokeApiService;

    public PokemonViewModelFactory(PokeApiService pokeApiService) {
        this.pokeApiService = pokeApiService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PokemonViewModel.class)) {
            return (T) new PokemonViewModel(pokeApiService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}