package com.example.whosthat;

import com.google.gson.annotations.SerializedName;

public class PokemonModel {
    public String name;

    @SerializedName("sprites")
    public Sprites sprites;

    public static class Sprites {
        @SerializedName("front_default")
        public String frontDefault;
    }
}