package com.example.whosthat;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.whosthat.game.Round;
import com.example.whosthat.game.Scoring;

/** Saved per-game records that drive the achievements. */
public class HighScoreManager {
    private static final String PREF_NAME = "HighScores";
    private static final String KEY_SECRET_ACHIEVEMENT = "SecretAchievement";

    /** Correct guesses faster than this unlock the "lightning" achievement. */
    public static final int LIGHTNING_SECONDS = 5;

    private final SharedPreferences prefs;

    public HighScoreManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Keys predate GameMode, keep them so existing records survive updates
    private static String streakKey(GameMode game) {
        return game == GameMode.POKEMON ? "HighStreakPokemon" : "HighStreakLeagueOfLegends";
    }

    private static String bestScoreKey(GameMode game) {
        return game == GameMode.POKEMON ? "BestScorePokemon" : "BestScoreLeagueOfLegends";
    }

    private static String firstTryKey(GameMode game) {
        return "FirstTryCount_" + game.key;
    }

    private static String lightningKey(GameMode game) {
        return "Lightning_" + game.key;
    }

    private static String clutchKey(GameMode game) {
        return "Clutch_" + game.key;
    }

    public int getHighStreak(GameMode game) {
        return prefs.getInt(streakKey(game), 0);
    }

    public int getBestScore(GameMode game) {
        return prefs.getInt(bestScoreKey(game), 0);
    }

    /** Rounds won on the very first guess. */
    public int getFirstTryCount(GameMode game) {
        return prefs.getInt(firstTryKey(game), 0);
    }

    public boolean isLightningUnlocked(GameMode game) {
        return prefs.getBoolean(lightningKey(game), false);
    }

    /** Won a round with the last possible guess. */
    public boolean isClutchUnlocked(GameMode game) {
        return prefs.getBoolean(clutchKey(game), false);
    }

    public boolean isSecretAchievementUnlocked() {
        return prefs.getBoolean(KEY_SECRET_ACHIEVEMENT, false);
    }

    public void unlockSecretAchievement() {
        prefs.edit().putBoolean(KEY_SECRET_ACHIEVEMENT, true).apply();
    }

    /** Records a won round: streak and run score so far, and how the round was won. */
    public void recordRoundWon(GameMode game, int streak, int runScore, Scoring.Breakdown points) {
        SharedPreferences.Editor editor = prefs.edit();
        if (streak > getHighStreak(game)) {
            editor.putInt(streakKey(game), streak);
        }
        if (runScore > getBestScore(game)) {
            editor.putInt(bestScoreKey(game), runScore);
        }
        if (points.guessNumber == 1) {
            editor.putInt(firstTryKey(game), getFirstTryCount(game) + 1);
        }
        if (points.seconds < LIGHTNING_SECONDS) {
            editor.putBoolean(lightningKey(game), true);
        }
        if (points.guessNumber == Round.MAX_GUESSES) {
            editor.putBoolean(clutchKey(game), true);
        }
        editor.apply();
    }
}
