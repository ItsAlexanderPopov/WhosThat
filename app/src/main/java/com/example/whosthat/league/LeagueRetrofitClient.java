package com.example.whosthat.league;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LeagueRetrofitClient {
    public static final String BASE_URL = "https://ddragon.leagueoflegends.com/";
    private static Retrofit retrofit = null;
    private static LeagueApiService service = null;

    private LeagueRetrofitClient() {
        // Private constructor to prevent instantiation
    }

    public static synchronized Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static synchronized LeagueApiService getLeagueApiService() {
        if (service == null) {
            service = getClient().create(LeagueApiService.class);
        }
        return service;
    }
}
