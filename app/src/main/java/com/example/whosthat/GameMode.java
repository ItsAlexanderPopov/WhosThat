package com.example.whosthat;

/** The two guessing games; {@link #key} is also used in achievement ids and saved-score keys. */
public enum GameMode {
    POKEMON("poke", "Pokemon"),
    LEAGUE("lol", "League of Legends");

    public final String key;
    public final String displayName;

    GameMode(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }
}
