package com.example.whosthat;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreManager {
    private static final String PREF_NAME = "HighScores";
    private static final String KEY_SCORE_POKE = "HighScorePokemon";
    private static final String KEY_SCORE_LOL = "HighScoreLeagueOfLegends";

    private SharedPreferences prefs;

    public HighScoreManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getHighScorePoke() {
        int score = prefs.getInt(KEY_SCORE_POKE, 0);
        return score;
    }
    public int getHighScoreLeagueOfLegends() {
        int score = prefs.getInt(KEY_SCORE_LOL, 0);
        return score;
    }

    public void updateHighScorePoke(int newScore) {
        int currentScore = getHighScorePoke();
        if (newScore > currentScore) {
            saveHighScorePoke(newScore);
        }
    }

    public void updateHighScoreLeagueOfLegends(int newScore) {
        int currentScore = getHighScoreLeagueOfLegends();
        if (newScore > currentScore) {
            saveHighScoreLeagueOfLegends(newScore);
        }
    }

    public void saveHighScorePoke(int score) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SCORE_POKE, score);
        editor.apply();
    }

    public void saveHighScoreLeagueOfLegends(int score) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SCORE_LOL, score);
        editor.apply();
    }
}