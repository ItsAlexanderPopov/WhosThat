package com.example.whosthat;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreManager {
    private static final String PREF_NAME = "HighScores";
    private static final String KEY_STREAK_POKE = "HighStreakPokemon";
    private static final String KEY_STREAK_LOL = "HighStreakLeagueOfLegends";

    private SharedPreferences prefs;

    public HighScoreManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getHighStreakPokemon() {
        return prefs.getInt(KEY_STREAK_POKE, 0);
    }

    public int getHighStreakLeagueOfLegends() {
        return prefs.getInt(KEY_STREAK_LOL, 0);
    }

    public void updateHighStreakPokemon(int newStreak) {
        int currentStreak = getHighStreakPokemon();
        if (newStreak > currentStreak) {
            saveHighStreakPokemon(newStreak);
        }
    }

    public void updateHighStreakLeagueOfLegends(int newStreak) {
        int currentStreak = getHighStreakLeagueOfLegends();
        if (newStreak > currentStreak) {
            saveHighStreakLeagueOfLegends(newStreak);
        }
    }

    private void saveHighStreakPokemon(int streak) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_STREAK_POKE, streak);
        editor.apply();
    }

    private void saveHighStreakLeagueOfLegends(int streak) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_STREAK_LOL, streak);
        editor.apply();
    }
}