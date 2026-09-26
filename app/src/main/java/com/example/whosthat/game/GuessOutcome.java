package com.example.whosthat.game;

/** What happened after the player submitted a guess (or skipped). */
public final class GuessOutcome {
    public enum Type {
        /** No round in progress (still loading, or showing the previous answer). */
        NOT_READY,
        /** Not a name from the list. */
        INVALID,
        /** Already guessed this round; no penalty. */
        DUPLICATE,
        WRONG,
        WON,
        /** Out of guesses, or skipped. */
        LOST
    }

    public final Type type;
    public final String answer;
    public final Scoring.Breakdown points;
    public final int guessesLeft;
    /** Score of the current run after this guess; for LOST, the final score of the run that just ended. */
    public final int runScore;

    GuessOutcome(Type type, String answer, Scoring.Breakdown points, int guessesLeft, int runScore) {
        this.type = type;
        this.answer = answer;
        this.points = points;
        this.guessesLeft = guessesLeft;
        this.runScore = runScore;
    }

    static GuessOutcome of(Type type) {
        return new GuessOutcome(type, null, null, 0, 0);
    }
}
