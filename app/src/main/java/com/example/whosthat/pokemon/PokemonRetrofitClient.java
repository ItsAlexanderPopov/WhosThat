package com.example.whosthat.pokemon;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonRetrofitClient {
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    private static Retrofit retrofit = null;

    private PokemonRetrofitClient() {
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

    public static PokeApiService getPokeApiService() {
        return getClient().create(PokeApiService.class);
    }
}