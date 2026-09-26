package com.example.whosthat.pokemon;

import com.google.gson.annotations.SerializedName;

public class PokemonModel {
    // API name, e.g. "mr-mime", "nidoran-f", "farfetchd"
    public String name;

    @SerializedName("sprites")
    public Sprites sprites;

    public static class Sprites {
        @SerializedName("front_default")
        public String frontDefault;

        @SerializedName("other")
        public Other other;
    }

    public static class Other {
        @SerializedName("official-artwork")
        public Artwork officialArtwork;
    }

    public static class Artwork {
        @SerializedName("front_default")
        public String frontDefault;
    }

    /** High-resolution official artwork when available, otherwise the 96px game sprite. */
    public String getImageUrl() {
        if (sprites == null) {
            return null;
        }
        if (sprites.other != null && sprites.other.officialArtwork != null
                && sprites.other.officialArtwork.frontDefault != null) {
            return sprites.other.officialArtwork.frontDefault;
        }
        return sprites.frontDefault;
    }
}
