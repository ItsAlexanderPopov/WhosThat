package com.example.whosthat.game;

import java.util.Locale;

/**
 * Round points: start from {@link #BASE_POINTS}, lose points for time taken, wrong guesses and
 * hints, never dropping below {@link #MIN_POINTS} for a correct answer. Guessing on the first
 * try earns a bonus.
 */
public final class Scoring {
    public static final int BASE_POINTS = 1000;
    public static final int TIME_PENALTY_PER_SECOND = 10;
    public static final int MAX_TIME_PENALTY = 500;
    public static final int WRONG_GUESS_PENALTY = 150;
    public static final int FIRST_TRY_BONUS = 250;
    public static final int MIN_POINTS = 50;

    private Scoring() {
    }

    public static Breakdown calculate(long elapsedMs, int wrongGuesses, int hintCost) {
        int seconds = (int) Math.max(0, elapsedMs / 1000);
        int timePenalty = Math.min(MAX_TIME_PENALTY, seconds * TIME_PENALTY_PER_SECOND);
        int guessPenalty = wrongGuesses * WRONG_GUESS_PENALTY;
        int bonus = wrongGuesses == 0 ? FIRST_TRY_BONUS : 0;
        int total = Math.max(MIN_POINTS, BASE_POINTS - timePenalty - guessPenalty - hintCost + bonus);
        return new Breakdown(seconds, timePenalty, wrongGuesses + 1, guessPenalty, hintCost, bonus, total);
    }

    public static final class Breakdown {
        public final int seconds;
        public final int timePenalty;
        /** 1-based number of the guess that would be / was correct. */
        public final int guessNumber;
        public final int guessPenalty;
        public final int hintPenalty;
        public final int bonus;
        public final int total;

        Breakdown(int seconds, int timePenalty, int guessNumber, int guessPenalty, int hintPenalty, int bonus, int total) {
            this.seconds = seconds;
            this.timePenalty = timePenalty;
            this.guessNumber = guessNumber;
            this.guessPenalty = guessPenalty;
            this.hintPenalty = hintPenalty;
            this.bonus = bonus;
            this.total = total;
        }

        public String describe() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(Locale.US, "+%d pts · guess #%d · %ds", total, guessNumber, seconds));
            if (hintPenalty > 0) {
                sb.append(String.format(Locale.US, " · hints -%d", hintPenalty));
            }
            if (bonus > 0) {
                sb.append(" · first try!");
            }
            return sb.toString();
        }
    }
}
