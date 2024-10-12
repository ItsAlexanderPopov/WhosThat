package com.example.whosthat.league;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LeagueRetrofitClient {
    private static final String BASE_URL = "https://ddragon.leagueoflegends.com/cdn/14.20.1/";
    private static Retrofit retrofit = null;

    private LeagueRetrofitClient() {
        // Private constructor to prevent instantiation
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static LeagueApiService getLeagueApiService() {
        return getClient().create(LeagueApiService.class);
    }
}