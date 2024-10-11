package com.example.whosthat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LeagueApiService {
    @GET("data/en_US/champion.json")
    Call<LeagueChampionModel.ChampionList> getChampionList();

    @GET("img/champion/splash/{championId}_0.jpg")
    Call<Void> getChampionSplashArt(@Path("championId") String championId);
}