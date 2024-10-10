package com.example.whosthat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokeApiService {
    @GET("pokemon/{id}")
    Call<PokemonModel> getPokemon(@Path("id") int id);
}