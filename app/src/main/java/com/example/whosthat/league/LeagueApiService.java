package com.example.whosthat.league;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LeagueApiService {
    @GET("data/en_US/champion.json")
    Call<LeagueChampionModel.ChampionList> getChampionList();

    @GET("data/en_US/champion/{championId}.json")
    Call<LeagueChampionModel> getChampionData(@Path("championId") String championId);
}