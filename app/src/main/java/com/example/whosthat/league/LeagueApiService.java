package com.example.whosthat.league;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LeagueApiService {
    // Newest patch first, e.g. ["16.11.1", "16.10.1", ...]
    @GET("api/versions.json")
    Call<List<String>> getVersions();

    @GET("cdn/{version}/data/en_US/champion.json")
    Call<LeagueChampionModel.ChampionList> getChampionList(@Path("version") String version);
}
