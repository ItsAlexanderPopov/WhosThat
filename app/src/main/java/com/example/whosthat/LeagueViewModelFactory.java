package com.example.whosthat;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LeagueViewModelFactory implements ViewModelProvider.Factory {
    private final LeagueApiService leagueApiService;

    public LeagueViewModelFactory(LeagueApiService leagueApiService) {
        this.leagueApiService = leagueApiService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LeagueOfLegendsViewModel.class)) {
            return (T) new LeagueOfLegendsViewModel(leagueApiService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}